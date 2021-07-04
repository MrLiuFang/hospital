package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.SystemAlarmHandleDto;
import com.lion.common.dto.UpdateStateDto;
import com.lion.common.enums.SystemAlarmState;
import com.lion.common.enums.Type;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.SystemAlarmDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.ListSystemAlarmVo;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmDetailsVo;
import com.lion.event.entity.vo.SystemAlarmVo;
import com.lion.event.service.SystemAlarmReportService;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.rule.AlarmExposeService;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.RestrictedAreaExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
@Service
@Log
public class SystemAlarmServiceImpl implements SystemAlarmService {

    @Autowired
    private SystemAlarmDao alarmDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private RestrictedAreaExposeService restrictedAreaExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private AlarmExposeService alarmExposeService;

    @Autowired
    private SystemAlarmReportService systemAlarmReportService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SystemAlarm save(SystemAlarm systemAlarm) {
        return alarmDao.save(systemAlarm);
    }

    @Override
    public void updateSdt(String id) {
        alarmDao.updateSdt(id);
    }


    @Override
    public void unalarm(String id) throws JsonProcessingException {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(userId)) {
            User user = userExposeService.findById(userId);
            if (Objects.nonNull(user)) {
                Query query = new Query();
                Criteria criteria = new Criteria();
//                if (Objects.nonNull(uuid)) {
//                    criteria.and("ui").is(uuid);
//                }
                if (Objects.nonNull(id)) {
                    criteria.and("_id").is(id);
                }
                query.addCriteria(criteria);
                SystemAlarm systemAlarm = mongoTemplate.findOne(query,SystemAlarm.class);
                alarmDao.unalarm( id, userId, user.getName());
                if (Objects.nonNull(systemAlarm)) {
                    updateDeviceState(systemAlarm);
                }
            }
        }
    }

    @Override
    public void alarmReport(AlarmReportDto alarmReportDto) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(userId)) {
            User user = userExposeService.findById(userId);
            if (Objects.nonNull(user)){
                alarmDao.alarmReport(alarmReportDto, userId,user.getName() );
            }
        }

    }

    @Override
    public void oldAlarmToNewAlarm(String id) throws JsonProcessingException {
        SystemAlarm systemAlarm = alarmDao.findId(id);
        if (Objects.nonNull(systemAlarm)){
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(Type.instance(systemAlarm.getTy()) );
            if (Objects.nonNull(systemAlarm.getTi())){
                systemAlarmDto.setTagId(systemAlarm.getTi());
            }
            if (Objects.nonNull(systemAlarm.getAi())){
                systemAlarmDto.setAssetsId(systemAlarm.getAi());
            }
            if (Objects.nonNull(systemAlarm.getPi())){
                systemAlarmDto.setPeopleId(systemAlarm.getPi());
            }
            if (Objects.nonNull(systemAlarm.getDvi())){
                systemAlarmDto.setDeviceId(systemAlarm.getDvi());
            }
            systemAlarmDto.setSystemAlarmType(SystemAlarmType.instance(systemAlarm.getSat()));
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setRegionId(systemAlarm.getRi());
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        }
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        return alarmDao.groupCount(buildFloorId, map);
    }

    @Override
    public Map<String, Integer> groupCount(Long departmentId) {
        return alarmDao.groupCount(departmentId);
    }

    @Override
    public List<SystemAlarm> find(Long userId, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return alarmDao.find(userId, ua, startDateTime, endDateTime);
    }

    @Override
    public SystemAlarm findLast(Long pi) {
        return alarmDao.findLast(pi);
    }

    @Override
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return alarmDao.list(lionPage,departmentIds,ua,ri , alarmType ,tagIds, startDateTime, endDateTime);
    }

    @Override
    public SystemAlarm findOne(Long pi, Long ai, Long dvi, Long ti) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        if (Objects.nonNull(ai)) {
            criteria.and("ai").is(ai);
        }
        if (Objects.nonNull(dvi)) {
            criteria.and("dvi").is(dvi);
        }
        if (Objects.nonNull(ti)) {
            criteria.and("ti").is(ti);
        }
        criteria.and("dt").gte(LocalDateTime.now().minusDays(30));
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query,SystemAlarm.class);
        return systemAlarm;
    }

    @Override
    public IPageResultData<List<ListSystemAlarmVo>> list(Long pi, Long ri, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        if (Objects.nonNull(ri)) {
            criteria.and("ri").is(ri);
        }
        criteria.and("ua").in(SystemAlarmState.UNTREATED.getKey(),SystemAlarmState.CALL.getKey());
        if (Objects.isNull(startDateTime)){
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
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
//        long count = mongoTemplate.count(query, SystemAlarm.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        List<ListSystemAlarmVo> returnList = new ArrayList<>();
        items.forEach(systemAlarm -> {
            ListSystemAlarmVo vo = new ListSystemAlarmVo();
            BeanUtils.copyProperties(systemAlarm,vo);
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            vo.setAlarmContent(systemAlarmType.getDesc());
            vo.setAlarmCode(systemAlarmType.getName());
            returnList.add(vo);
        });
        IPageResultData<List<ListSystemAlarmVo>> pageResultData =new PageResultData<>(returnList,lionPage,0L);
        return pageResultData;
    }

    @Override
    public SystemAlarmDetailsVo details(String id) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(id)) {
            criteria.and("_id").is(id);
        }
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query,SystemAlarm.class);
        if (Objects.nonNull(systemAlarm)){
            SystemAlarmDetailsVo vo = new SystemAlarmDetailsVo();
            BeanUtils.copyProperties(systemAlarm,vo);
            vo.setType(Type.instance(systemAlarm.getTy()));
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            if (Objects.nonNull(systemAlarmType)) {
                vo.setAlarmCode(systemAlarmType.getName());
                vo.setAlarmContent(systemAlarmType.getDesc());
            }
            if (Objects.equals(systemAlarm.getTy(),Type.STAFF.getKey())) {
                User user = userExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(user)){
                    vo.setUserName(user.getName());
                    vo.setUserNumber(user.getNumber());
                    vo.setTitle(user.getName());
                    vo.setImg(user.getHeadPortrait());
                    vo.setImgUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.PATIENT.getKey())) {
                Patient patient = patientExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(patient)) {
                    vo.setPatientName(patient.getName());
                    vo.setTitle(patient.getName());
                    vo.setImg(patient.getHeadPortrait());
                    vo.setImgUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                }
                List<RestrictedArea> list = restrictedAreaExposeService.find(systemAlarm.getPi(), PersonType.PATIENT);
                List<String> restrictedAreaStringList = new ArrayList<>();
                list.forEach(restrictedArea -> {
                    Region region = regionExposeService.findById(restrictedArea.getRegionId());
                    restrictedAreaStringList.add(Objects.isNull(region)?"":region.getName());
                });
                vo.setRestrictedArea(restrictedAreaStringList);
            }
            if (Objects.equals(systemAlarm.getTy(),Type.MIGRANT.getKey())) {
                TemporaryPerson temporaryPerson = temporaryPersonExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(temporaryPerson)) {
                    vo.setTemporaryPersonName(temporaryPerson.getName());
                    vo.setTitle(temporaryPerson.getName());
                    vo.setImg(temporaryPerson.getHeadPortrait());
                    vo.setImgUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                }
            }
            if (Objects.nonNull(systemAlarm.getTi())) {
                Tag tag = tagExposeService.findById(systemAlarm.getTi());
                if (Objects.nonNull(tag)) {
                    vo.setTagCode(tag.getTagCode());
                    vo.setTitle(tag.getDeviceName());
                    vo.setTagType(tag.getType());
                    vo.setTagPurpose(tag.getPurpose());
                    vo.setBattery(tag.getBattery());
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.ASSET.getKey())) {
                Assets assets = assetsExposeService.findById(systemAlarm.getAi());
                if (Objects.nonNull(assets)) {
                    vo.setAssetsCode(assets.getCode());
                    vo.setAssetsName(assets.getName());
                    vo.setTitle(assets.getName());
                    vo.setImg(assets.getImg());
                    vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.DEVICE.getKey())) {
                Device device = deviceExposeService.findById(systemAlarm.getDvi());
                if (Objects.nonNull(device)) {
                    vo.setBattery(device.getBattery());
                    vo.setDeviceName(device.getName());
                    vo.setDeviceCode(device.getCode());
                    vo.setImg(device.getImg());
                    vo.setImgUrl(fileExposeService.getUrl(device.getImg()));
                }
            }
            if (Objects.nonNull(systemAlarm.getAli())) {
                Alarm alarm = alarmExposeService.findById(systemAlarm.getAli());
                if (Objects.nonNull(alarm)){
                    vo.setBlueCode(alarm.getBlueCode());
                }
            }
            if (Objects.nonNull(systemAlarm.getUui())) {
                User user = userExposeService.findById(systemAlarm.getUui());
                if (Objects.nonNull(user)) {
                    vo.setUuHeadPortrait(user.getHeadPortrait());
                    vo.setUuHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            vo.setSystemAlarmReportDetailsVos(systemAlarmReportService.list(vo.get_id()));
            return vo;
        }
        return null;
    }

    @Override
    public void updateState(SystemAlarmHandleDto systemAlarmDto) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.equals(systemAlarmDto.getState(),SystemAlarmState.CANCEL_CALL)) {
            log.info("20");
            if (Objects.nonNull(systemAlarmDto.getPeopleId())) {
                log.info("21");
                criteria.and("pi").is(systemAlarmDto.getPeopleId());
                criteria.and("ua").is(SystemAlarmState.CALL.getKey());
            }
        }else if (Objects.equals(systemAlarmDto.getState(),SystemAlarmState.WELL_KNOWN)) {
            log.info("22");
            if (Objects.nonNull(systemAlarmDto.getRegionId())) {
                log.info("23");
                criteria.and("ri").is(systemAlarmDto.getRegionId());
                criteria.and("ua").is(SystemAlarmState.UNTREATED.getKey());
            }
        }
        criteria.and("dt").gte(LocalDateTime.now().minusDays(30));
        query.addCriteria(criteria);
        List<SystemAlarm> items = mongoTemplate.find(query,SystemAlarm.class);
        items.forEach(systemAlarm -> {
            log.info(String.valueOf(systemAlarm.getPi()));
            Query queryUpdate = new Query();
            queryUpdate.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
            Update update = new Update();
            update.set("ua", systemAlarmDto.getState().getKey());
            mongoTemplate.updateFirst(queryUpdate, update, "system_alarm");
            redisTemplate.opsForValue().set(RedisConstants.UNALARM+systemAlarm.get_id(),true,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            updateDeviceState(systemAlarm);
        });
    }

    private void updateDeviceState(SystemAlarm systemAlarm ){
        UpdateStateDto updateStateDto = new UpdateStateDto();
        updateStateDto.setType(Type.instance(systemAlarm.getTy()));
        updateStateDto.setState(1);
        if (Objects.equals(Type.STAFF,updateStateDto.getType()) || Objects.equals(Type.PATIENT,updateStateDto.getType()) || Objects.equals(Type.MIGRANT,updateStateDto.getType())) {
            updateStateDto.setId(systemAlarm.getPi());
        }else if (Objects.equals(Type.ASSET,updateStateDto.getType())) {
            updateStateDto.setId(systemAlarm.getAi());
        }else if (Objects.equals(Type.DEVICE,updateStateDto.getType())) {
            updateStateDto.setId(systemAlarm.getDvi());
        }else if (Objects.equals(Type.HUMIDITY,updateStateDto.getType()) || Objects.equals(Type.TEMPERATURE,updateStateDto.getType())) {
            updateStateDto.setId(systemAlarm.getTi());
        }
        try {
            rocketMQTemplate.syncSend(TopicConstants.UPDATE_STATE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(updateStateDto)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
