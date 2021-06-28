package com.lion.event.dao.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.SystemAlarmState;
import com.lion.common.enums.Type;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.SystemAlarmDaoEx;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmVo;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:22
 **/
@Log
public class SystemAlarmDaoImpl implements SystemAlarmDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @Override
    public void updateSdt(String uuid) {
        SystemAlarm systemAlarm = findUuid(uuid);
        if (Objects.nonNull(systemAlarm)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
            Update update = new Update();
            update.set("sdt", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
    }

    @Override
    public void unalarm(String uuid, String id, Long userId, String userName) {
        SystemAlarm systemAlarm =null;
        if (!StringUtils.hasText(id) && StringUtils.hasText(uuid)) {
            systemAlarm = findUuid(uuid);
            id = systemAlarm.get_id();
        }
        if (StringUtils.hasText(id)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("ua", SystemAlarmState.PROCESSED.getKey());
            update.set("uui",userId);
            update.set("uun",userName);
            update.set("udt",LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
        redisTemplate.opsForValue().set(RedisConstants.UNALARM+uuid,true,24, TimeUnit.DAYS);
    }

    @Override
    public void alarmReport(AlarmReportDto alarmReportDto, Long userId, String userName) {
        if (Objects.nonNull(alarmReportDto) && StringUtils.hasText(alarmReportDto.getId())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(alarmReportDto.getId()));
            Update update = new Update();
            update.set("re",alarmReportDto.getReport());
            update.set("rnu",alarmReportDto.getNumber());
            update.set("rui",userId);
            update.set("run",userName);
            update.set("rdt",LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        LocalDateTime now = LocalDateTime.now();
        match = BasicDBObjectUtil.put(match,"$match","bfi",new BasicDBObject("$eq",buildFloorId) );
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",now.minusDays(30)).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$ri");
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
        List<RegionStatisticsDetails> list = new ArrayList<RegionStatisticsDetails>();
        aggregateIterable.forEach(document -> {
            if (document.containsKey("_id")) {
                Long regionId = document.getLong("_id");
                Integer count =document.getInteger("count");
                if (Objects.nonNull(regionId)  && Objects.nonNull(count) && count>0) {
                    if (map.containsKey(regionId)){
                        RegionStatisticsDetails regionStatisticsDetails = map.get(regionId);
                        regionStatisticsDetails.setIsAlarm(true);
                    }
                }
            }
        });
        return map;
    }

    @Override
    public Map<String, Integer> groupCount(Long departmentId) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        LocalDateTime now = LocalDateTime.now();
        match = BasicDBObjectUtil.put(match,"$match","sdi",new BasicDBObject("$eq",departmentId) );
//        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",LocalDateTime.of(now.toLocalDate(), LocalTime.MIN) ).append("$lte",now));
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",now.toLocalDate().minusDays(30) ).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$sdi");
        group = BasicDBObjectUtil.put(group,"$group","allAlarmCount",new BasicDBObject("$sum",1));
        group = BasicDBObjectUtil.put(group,"$group","unalarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ua",false?1:0})})).append("then",1).append("else",0))));
        group = BasicDBObjectUtil.put(group,"$group","alarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ua",true?1:0})})).append("then",1).append("else",0))));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
        Map<String, Integer> map = new HashMap<>();
        aggregateIterable.forEach(document -> {
            map.put("allAlarmCount",document.getInteger("allAlarmCount"));
            map.put("unalarmCount",document.getInteger("unalarmCount"));
            map.put("alarmCount",document.getInteger("alarmCount"));
        });
        return map;
    }

    @Override
    public List<SystemAlarm> find(Long userId, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria.and("pi").is(userId);
        }else {
            return null;
        }
        if (Objects.equals(false,ua)) {
            criteria.and("ua").in(SystemAlarmState.CALL.getKey(),SystemAlarmState.UNTREATED.getKey());
        }else if (Objects.equals(true,ua)) {
            criteria.and("ua").in(SystemAlarmState.CANCEL_CALL.getKey(),SystemAlarmState.PROCESSED.getKey(),SystemAlarmState.WELL_KNOWN.getKey());
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator(Criteria.where("dt").gte(startDateTime), Criteria.where("dt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("dt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("dt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(0,99999, Sort.by(Sort.Direction.DESC,"dt"));
        query.with(pageRequest);
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
        List<SystemAlarm> list = new ArrayList<>();
        if (Objects.nonNull(items) && items.size()>0){
            items.forEach(systemAlarm -> {
                list.add(systemAlarm);
            });
        }
        return list;
    }

    @Override
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            criteria.and("sdi").in(departmentIds);
        }
        if ( Objects.equals(ua,false)) {
            criteria.and("ua").in(SystemAlarmState.CALL.getKey(),SystemAlarmState.UNTREATED.getKey());
        }else if (Objects.equals(ua,true)) {
            criteria.and("ua").in(SystemAlarmState.CANCEL_CALL.getKey(),SystemAlarmState.PROCESSED.getKey(),SystemAlarmState.WELL_KNOWN.getKey());
        }
        if (Objects.nonNull(ri) && ri.size()>0) {
            criteria.and("ri").in(ri);
        }
        if (Objects.nonNull(alarmType)) {
            criteria.and("ty").is(alarmType.getKey());
        }
        if (Objects.nonNull(tagIds) && tagIds.size()>0) {
            criteria.and("ti").in(tagIds);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator(Criteria.where("dt").gte(startDateTime), Criteria.where("dt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime)) {
            criteria.and("dt").gte(startDateTime);
        }else if (Objects.nonNull(endDateTime)) {
            criteria.and("dt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, SystemAlarm.class);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"sdt"));
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
        List<SystemAlarmVo> list = new ArrayList<>();
        if (Objects.nonNull(items) && items.size()>0){
            items.forEach(systemAlarm -> {
                SystemAlarmVo vo = new SystemAlarmVo();
                BeanUtils.copyProperties(systemAlarm,vo);
                vo.setUa(SystemAlarmState.instance(systemAlarm.getUa()));
                vo.setType(Type.instance(systemAlarm.getTy()));
                vo.setDeviceDateTime(systemAlarm.getDt());
                vo.setSortDateTime(systemAlarm.getSdt());
                if (Objects.nonNull(systemAlarm.getTi())) {
                    Tag tag = tagExposeService.findById(systemAlarm.getTi());
                    if (Objects.nonNull(tag)){
                        vo.setTagCode(tag.getTagCode());
                        vo.setTagType(tag.getType());
                    }
                }
                if (Objects.nonNull(systemAlarm.getSat())) {
                    SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
                    vo.setAlarmContent(systemAlarmType.getDesc());
                    vo.setAlarmCode(systemAlarmType.getName());
                }
                if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.STAFF.getKey())) {
                    if (Objects.nonNull(systemAlarm.getPi())) {
                        User user = userExposeService.findById(systemAlarm.getPi());
                        if (Objects.nonNull(user)) {
                            vo.setTitle(user.getName());
                            vo.setImgId(user.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.ASSET.getKey())) {
                    if (Objects.nonNull(systemAlarm.getAi())) {
                        Assets assets = assetsExposeService.findById(systemAlarm.getAi());
                        if (Objects.nonNull(assets)) {
                            vo.setTitle(assets.getName());
                            vo.setImgId(assets.getImg());
                            vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) &&( Objects.equals(systemAlarm.getTy(),Type.TEMPERATURE.getKey()) ||Objects.equals(systemAlarm.getTy(),Type.HUMIDITY.getKey()) )) {
                    if (Objects.nonNull(systemAlarm.getTi())) {
                        Tag tag = tagExposeService.findById(systemAlarm.getTi());
                        if (Objects.nonNull(tag)) {
                            vo.setTitle(tag.getTagCode());
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.PATIENT.getKey())) {
                    if (Objects.nonNull(systemAlarm.getPi())) {
                        Patient patient = patientExposeService.findById(systemAlarm.getPi());
                        if (Objects.nonNull(patient)){
                            vo.setTitle(patient.getName());
                            vo.setImgId(patient.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.MIGRANT.getKey())) {
                    if (Objects.nonNull(systemAlarm.getPi())) {
                        TemporaryPerson temporaryPerson = temporaryPersonExposeService.findById(systemAlarm.getPi());
                        if (Objects.nonNull(temporaryPerson)){
                            vo.setTitle(temporaryPerson.getName());
                            vo.setImgId(temporaryPerson.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.DEVICE.getKey())) {
                    if (Objects.nonNull(systemAlarm.getDvi())) {
                        Device device = deviceExposeService.findById(systemAlarm.getDvi());
                        if (Objects.nonNull(device)) {
                            vo.setTitle(device.getName());
                            vo.setImgId(device.getImg());
                            vo.setImgUrl(fileExposeService.getUrl(device.getImg()));
                        }
                    }
                }
                list.add(vo);
            });
        }
        return new PageResultData<>(list,lionPage,count);
    }

    @Override
    public SystemAlarm findUuid(String uuid){
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        return systemAlarm;
    }

    @Override
    public SystemAlarm findId(String id) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        query.addCriteria(criteria);
//        query.with(Sort.by(Sort.Order.desc("sdt")));
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        return systemAlarm;
    }


}
