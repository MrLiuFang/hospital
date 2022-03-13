package com.lion.event.dao.impl;

import cn.hutool.core.util.NumberUtil;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
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
    public void updateSdt(String id) {
        if (StringUtils.hasText(id)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("sdt", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
    }

    @Override
    public void unalarm(String id, Long userId, String userName) {
        if (StringUtils.hasText(id)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("ua", SystemAlarmState.PROCESSED.getKey());
            update.set("uui",userId);
            update.set("uun",userName);
            update.set("udt",LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
            redisTemplate.opsForValue().set(RedisConstants.UNALARM+id,true,24, TimeUnit.DAYS);
        }
    }

    @Override
    public void alarmReport(AlarmReportDto alarmReportDto, Long userId, String userName) {
        if (Objects.nonNull(alarmReportDto) && StringUtils.hasText(alarmReportDto.getId())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(alarmReportDto.getId()));
            Update update = new Update();
            update.set("re",alarmReportDto.getReport());
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
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",now.minusDays(30)).append("$lte",now));
        match = BasicDBObjectUtil.put(match,"$match","bfi",new BasicDBObject("$eq",buildFloorId) );
        match = BasicDBObjectUtil.put(match,"$match","ua",new BasicDBObject("$in",new Integer[]{SystemAlarmState.UNTREATED.getKey(),SystemAlarmState.CALL.getKey()}) );
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
                Integer count =NumberUtil.isInteger(String.valueOf(document.get("count")))?document.getInteger("count"):0;
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
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",LocalDateTime.of(now.toLocalDate().minusDays(3), LocalTime.MIN) ).append("$lte",now));
//        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",now.toLocalDate().minusDays(30) ).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$sdi");
        group = BasicDBObjectUtil.put(group,"$group","allAlarmCount",new BasicDBObject("$sum",1));
        group = BasicDBObjectUtil.put(group,"$group","unalarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$in",new Object[]{"$ua",new Integer[]{SystemAlarmState.UNTREATED.getKey(),SystemAlarmState.CALL.getKey()}})})).append("then",1).append("else",0))));
        group = BasicDBObjectUtil.put(group,"$group","alarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$in",new Object[]{"$ua",new Integer[]{SystemAlarmState.PROCESSED.getKey(),SystemAlarmState.CANCEL_CALL.getKey()}})})).append("then",1).append("else",0))));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
        Map<String, Integer> map = new HashMap<>();
        aggregateIterable.forEach(document -> {
            map.put("allAlarmCount",NumberUtil.isInteger(String.valueOf(document.get("allAlarmCount")))?document.getInteger("allAlarmCount"):0);
            map.put("unalarmCount",NumberUtil.isInteger(String.valueOf(document.get("unalarmCount")))?document.getInteger("unalarmCount"):0);
            map.put("alarmCount",NumberUtil.isInteger(String.valueOf(document.get("unalarmCount")))?document.getInteger("alarmCount"):0);
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
    public SystemAlarm findLast(Long pi) {
        return findLast(pi,null,null);
    }

    @Override
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime, String... sorts) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            criteria.and("sdi").in(departmentIds);
        }
        if (Objects.nonNull(ua) && Objects.equals(ua,false)) {
            criteria.and("ua").in(SystemAlarmState.CALL.getKey(),SystemAlarmState.UNTREATED.getKey());
        }else if (Objects.nonNull(ua) && Objects.equals(ua,true)) {
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
//            startDateTime = LocalDateTime.now().minusDays(30);
            startDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
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
        query.with(Sort.by(Sort.Direction.DESC,sorts));
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
        List<SystemAlarmVo> list = new ArrayList<>();
        HashMap<String,Object> cache = new HashMap<String,Object>();
        if (Objects.nonNull(items) && items.size()>0){
            items.forEach(systemAlarm -> {
                SystemAlarmVo vo = new SystemAlarmVo();
                BeanUtils.copyProperties(systemAlarm,vo);
                vo.setUa(SystemAlarmState.instance(systemAlarm.getUa()));
                vo.setType(Type.instance(systemAlarm.getTy()));
                vo.setDeviceDateTime(systemAlarm.getDt());
                vo.setSortDateTime(systemAlarm.getSdt());
                if (Objects.nonNull(systemAlarm.getTi())) {
                    Tag tag = null;
                    if (cache.containsKey(systemAlarm.getTi())) {
                        tag = (Tag) cache.get(systemAlarm.getTi());
                    }else {
                        com.lion.core.Optional<Tag> optional = tagExposeService.findById(systemAlarm.getTi());
                        if (optional.isPresent()) {
                            tag = optional.get();
                            cache.put(String.valueOf(systemAlarm.getTi()), tag);
                        }
                    }
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
                        User user = null;
                        if (cache.containsKey(systemAlarm.getPi())) {
                            user = (User) cache.get(systemAlarm.getPi());
                        }else {
                            com.lion.core.Optional<User> optionalUser = userExposeService.findById(systemAlarm.getPi());
                            if (optionalUser.isPresent()) {
                                user = optionalUser.get();
                                cache.put(String.valueOf(systemAlarm.getPi()), user);
                            }
                        }
                        if (Objects.nonNull(user)) {
                            vo.setTitle(user.getName());
                            vo.setImgId(user.getHeadPortrait());
                            String headPortraitUrl = null;
                            if (cache.containsKey(user.getHeadPortrait())) {
                                headPortraitUrl = (String) cache.get(user.getHeadPortrait());
                            }else {
                                headPortraitUrl = fileExposeService.getUrl(user.getHeadPortrait());
                                if (StringUtils.hasText(headPortraitUrl)) {
                                    cache.put(String.valueOf(user.getHeadPortrait()), headPortraitUrl);
                                }
                            }
                            vo.setImgUrl(headPortraitUrl);
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.ASSET.getKey())) {
                    if (Objects.nonNull(systemAlarm.getAi())) {
                        Assets assets = null;
                        if (cache.containsKey(systemAlarm.getAi())) {
                            assets = (Assets) cache.get(systemAlarm.getAi());
                        }else {
                            com.lion.core.Optional<Assets> optionalAssets = assetsExposeService.findById(systemAlarm.getAi());
                            if (optionalAssets.isPresent()) {
                                assets = optionalAssets.get();
                                cache.put(String.valueOf(systemAlarm.getAi()), assets);
                            }
                        }
                        if (Objects.nonNull(assets)) {
                            vo.setTitle(assets.getName());
                            vo.setImgId(assets.getImg());
                            String imgUrl = null;
                            if (cache.containsKey(assets.getImg())) {
                                imgUrl = (String) cache.get(assets.getImg());
                            }else {
                                imgUrl = fileExposeService.getUrl(assets.getImg());
                                if (StringUtils.hasText(imgUrl)) {
                                    cache.put(String.valueOf(assets.getImg()), imgUrl);
                                }
                            }
                            vo.setImgUrl(imgUrl);
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) &&( Objects.equals(systemAlarm.getTy(),Type.TEMPERATURE.getKey()) ||Objects.equals(systemAlarm.getTy(),Type.HUMIDITY.getKey()) )) {
                    if (Objects.nonNull(systemAlarm.getTi())) {
                        Tag tag = null;
                        if (cache.containsKey(systemAlarm.getTi())) {
                            tag = (Tag) cache.get(systemAlarm.getTi());
                        }else {
                            com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(systemAlarm.getTi());
                            if (optionalTag.isPresent()) {
                                tag = optionalTag.get();
                                cache.put(String.valueOf(systemAlarm.getTi()), tag);
                            }
                        }
                        if (Objects.nonNull(tag)) {
                            vo.setTitle(tag.getTagCode());
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.PATIENT.getKey())) {
                    if (Objects.nonNull(systemAlarm.getPi())) {
                        Patient patient = null;
                        if (cache.containsKey(systemAlarm.getPi())) {
                            patient = (Patient) cache.get(systemAlarm.getPi());
                        }else {
                            com.lion.core.Optional<Patient> optionalPatient = patientExposeService.findById(systemAlarm.getPi());
                            if (optionalPatient.isPresent()) {
                                patient = optionalPatient.get();
                                cache.put(String.valueOf(systemAlarm.getPi()), patient);
                            }
                        }
                        if (Objects.nonNull(patient)){
                            vo.setTitle(patient.getName());
                            vo.setImgId(patient.getHeadPortrait());
                            String imgUrl = null;
                            if (cache.containsKey(patient.getHeadPortrait())) {
                                imgUrl = (String) cache.get(patient.getHeadPortrait());
                            }else {
                                imgUrl = fileExposeService.getUrl(patient.getHeadPortrait());
                                if (StringUtils.hasText(imgUrl)) {
                                    cache.put(String.valueOf(patient.getHeadPortrait()), imgUrl);
                                }
                            }
                            vo.setImgUrl(imgUrl);
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.MIGRANT.getKey())) {
                    if (Objects.nonNull(systemAlarm.getPi())) {
                        TemporaryPerson temporaryPerson = null;
                        if (cache.containsKey(systemAlarm.getPi())) {
                            temporaryPerson = (TemporaryPerson) cache.get(systemAlarm.getPi());
                        }else {
                            com.lion.core.Optional<TemporaryPerson> optionalTemporaryPerson = temporaryPersonExposeService.findById(systemAlarm.getPi());
                            if (optionalTemporaryPerson.isPresent()) {
                                temporaryPerson = optionalTemporaryPerson.get();
                                cache.put(String.valueOf(systemAlarm.getPi()), temporaryPerson);
                            }
                        }
                        if (Objects.nonNull(temporaryPerson)){
                            vo.setTitle(temporaryPerson.getName());
                            vo.setImgId(temporaryPerson.getHeadPortrait());
                            String imgUrl = null;
                            if (cache.containsKey(temporaryPerson.getHeadPortrait())) {
                                imgUrl = (String) cache.get(temporaryPerson.getHeadPortrait());
                            }else {
                                imgUrl = fileExposeService.getUrl(temporaryPerson.getHeadPortrait());
                                if (StringUtils.hasText(imgUrl)) {
                                    cache.put(String.valueOf(temporaryPerson.getHeadPortrait()), imgUrl);
                                }
                            }
                            vo.setImgUrl(imgUrl);
                        }
                    }
                }else if (Objects.nonNull(systemAlarm.getTy()) && Objects.equals(systemAlarm.getTy(),Type.DEVICE.getKey())) {
                    if (Objects.nonNull(systemAlarm.getDvi())) {
                        Device device = null;
                        if (cache.containsKey(systemAlarm.getDvi())) {
                            device = (Device) cache.get(systemAlarm.getDvi());
                        }else {
                            com.lion.core.Optional<Device> optionalDevice = deviceExposeService.findById(systemAlarm.getDvi());
                            if (optionalDevice.isPresent()) {
                                device = optionalDevice.get();
                                cache.put(String.valueOf(systemAlarm.getDvi()), device);
                            }
                        }
                        if (Objects.nonNull(device)) {
                            vo.setTitle(device.getName());
                            vo.setImgId(device.getImg());
                            String imgUrl = null;
                            if (cache.containsKey(device.getImg())) {
                                imgUrl = (String) cache.get(device.getImg());
                            }else {
                                imgUrl = fileExposeService.getUrl(device.getImg());
                                if (StringUtils.hasText(imgUrl)) {
                                    cache.put(String.valueOf(device.getImg()), imgUrl);
                                }
                            }
                            vo.setImgUrl(imgUrl);
                        }
                    }
                }
                list.add(vo);
            });
        }
        return new PageResultData<>(list,lionPage,count);
    }

    @Override
    public List<Document> sevenDaysStatistics(Long departmentId) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(departmentId)) {
            match = BasicDBObjectUtil.put(match, "$match", "sdi", new BasicDBObject("$eq", departmentId));
        }
//        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",LocalDateTime.of(now.toLocalDate(), LocalTime.MIN) ).append("$lte",now));
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte", LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN )).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id",new BasicDBObject("$dateToString",new BasicDBObject("format","%Y-%m-%d").append("date","$dt")));
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
        List<Document> list = new ArrayList<>();
        aggregateIterable.forEach(document -> {
            list.add(document);
        });
        return list;
    }

    @Override
    public long todayDaysStatistics(Type type, LocalDateTime startDateTime, LocalDateTime endDateTime) {
//        List<Bson> pipeline = new ArrayList<Bson>();
//        BasicDBObject match = new BasicDBObject();
//        if (Objects.isNull(startDateTime)) {
//            startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN );
//        }
//        if (Objects.nonNull(type)) {
//            match = BasicDBObjectUtil.put(match, "$match", "ty", new BasicDBObject("$eq", type.getKey()));
//        }
//        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",startDateTime ).append("$lte",LocalDateTime.now()));
//        pipeline.add(match);
//        BasicDBObject group = new BasicDBObject();
//        group = BasicDBObjectUtil.put(group,"$group","_id",new BasicDBObject("$dateToString",new BasicDBObject("format","%Y-%m-%d").append("date","$dt")));
//        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
//        pipeline.add(group);
//        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
//        return aggregateIterable.first();

        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.isNull(endDateTime)) {
            endDateTime = LocalDateTime.now();
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator(Criteria.where("dt").gte(startDateTime), Criteria.where("dt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime)) {
            criteria.and("dt").gte(startDateTime);
        }else if (Objects.nonNull(endDateTime)) {
            criteria.and("dt").lte(endDateTime);
        }

        if (Objects.nonNull(type)) {
            criteria.and("ty").is(type.getKey());
        }
        query.addCriteria(criteria);
        return mongoTemplate.count(query, SystemAlarm.class);
//        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.group("_id"), Aggregation.count().as("countNum"));
//        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "system_alarm",Document.class);
//        List<Document> mappedResults = results.getMappedResults();
//        if (mappedResults.size()>0) {
//            Document document = mappedResults.get(0);
//            if (document.containsKey("countNum")){
//                return Long.valueOf(document.getInteger("countNum"));
//            }
//        }
//        return 0;
    }

    @Override
    public SystemAlarm findLastByAssetsId(Long assetsId) {
        return findLast(null,assetsId,null);
    }

    @Override
    public SystemAlarm findLastByTagId(Long tagId) {
        return findLast(null,null,tagId);
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
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        return systemAlarm;
    }

    private SystemAlarm findLast(Long pi,Long ai,Long ti) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.isNull(pi) && Objects.isNull(ai) && Objects.isNull(ti)) {
            return null;
        }
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        if (Objects.nonNull(ai)) {
            criteria.and("ai").is(ai);
        }
        if (Objects.nonNull(ti)) {
            criteria.and("ti").is(ti);
        }
        criteria.and("dt").gte(LocalDateTime.now().minusDays(90));
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(0,1, Sort.by(Sort.Direction.DESC,"dt"));
        query.with(pageRequest);
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
        if (Objects.nonNull(items) && items.size()>0){
            return items.get(0);
        }
        return null;
    }

}
