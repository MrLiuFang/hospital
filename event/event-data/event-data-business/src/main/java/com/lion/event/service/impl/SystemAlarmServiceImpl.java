package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.UpdateStateDto;
import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.SystemAlarmDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.ListSystemAlarmVo;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmDetailsVo;
import com.lion.event.entity.vo.SystemAlarmVo;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
@Service
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

    @Override
    public void save(SystemAlarm systemAlarm) {
        alarmDao.save(systemAlarm);
    }

    @Override
    public void updateSdt(String uuid) {
        alarmDao.updateSdt(uuid);
    }

    @Override
    public SystemAlarm find(String uuid) {
        return alarmDao.findUuid(uuid);
    }

    @Override
    public void unalarm(String uuid, String id) throws JsonProcessingException {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(userId)) {
            User user = userExposeService.findById(userId);
            if (Objects.nonNull(user)) {
                SystemAlarm exampleSystemAlarm = new SystemAlarm();
                if (Objects.nonNull(uuid)) {
                    exampleSystemAlarm.setUi(uuid);
                }
                if (Objects.nonNull(id)) {
                    exampleSystemAlarm.set_id(id);
                }
                exampleSystemAlarm.setUa(false);
                Example<SystemAlarm> example = Example.of(exampleSystemAlarm);
                Optional<SystemAlarm> optional = alarmDao.findOne(example);
                if (optional.isPresent()) {
                    SystemAlarm systemAlarm = optional.get();
                    UpdateStateDto updateStateDto = new UpdateStateDto();
                    updateStateDto.setType(Type.instance(systemAlarm.getTy()));
                    updateStateDto.setState(1);
                    if (Objects.equals(Type.STAFF,updateStateDto.getType()) || Objects.equals(Type.PATIENT,updateStateDto.getType()) || Objects.equals(Type.MIGRANT,updateStateDto.getType())) {
                        updateStateDto.setId(systemAlarm.getPi());
                    }else if (Objects.equals(Type.ASSET,updateStateDto.getType())) {
                        updateStateDto.setId(systemAlarm.getAi());
                    }else if (Objects.equals(Type.HUMIDITY,updateStateDto.getType()) || Objects.equals(Type.TEMPERATURE,updateStateDto.getType())) {
                        updateStateDto.setId(systemAlarm.getTi());
                    }
                    alarmDao.unalarm(uuid, id, userId, user.getName());
                    rocketMQTemplate.syncSend(TopicConstants.UPDATE_STATE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(updateStateDto)).build());
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
            systemAlarmDto.setUuid(UUID.randomUUID().toString());
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
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, Long ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return alarmDao.list(lionPage,departmentIds,ua,ri , alarmType ,tagIds, startDateTime, endDateTime);
    }

    @Override
    public SystemAlarm findOne(Long pi, Long ai, Long dvi, Long ti) {
        SystemAlarm exampleSystemAlarm = new SystemAlarm();
        if (Objects.nonNull(pi)) {
            exampleSystemAlarm.setPi(pi);
        }
        if (Objects.nonNull(ai)) {
            exampleSystemAlarm.setAi(ai);
        }
        if (Objects.nonNull(dvi)) {
            exampleSystemAlarm.setDvi(dvi);
        }
        if (Objects.nonNull(ti)) {
            exampleSystemAlarm.setTi(ti);
        }
        exampleSystemAlarm.setUa(false);
        Example<SystemAlarm> example = Example.of(exampleSystemAlarm);
        Optional<SystemAlarm> optional = alarmDao.findOne(example);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public IPageResultData<List<ListSystemAlarmVo>> list(Long pi, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        criteria.and("ua").is(false ? 1 : 0);
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
        SystemAlarm exampleSystemAlarm = new SystemAlarm();
        if (Objects.nonNull(id)) {
            exampleSystemAlarm.set_id(id);
        }
        Example<SystemAlarm> example = Example.of(exampleSystemAlarm);
        Optional<SystemAlarm> optional = alarmDao.findOne(example);
        if (optional.isPresent()){
            SystemAlarmDetailsVo vo = new SystemAlarmDetailsVo();
            SystemAlarm systemAlarm = optional.get();
            BeanUtils.copyProperties(systemAlarm,vo);
            vo.setType(Type.instance(systemAlarm.getTy()));
            if (Objects.equals(systemAlarm.getTy(),Type.STAFF.getKey())) {
                User user = userExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(user)){
                    vo.setUserName(user.getName());
                    vo.setUserNumber(user.getNumber());
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.PATIENT.getKey())) {
                Patient patient = patientExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(patient)) {
                    vo.setPatientName(patient.getName());
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.MIGRANT.getKey())) {
                TemporaryPerson temporaryPerson = temporaryPersonExposeService.findById(systemAlarm.getPi());
                if (Objects.nonNull(temporaryPerson)) {
                    vo.setTemporaryPersonName(temporaryPerson.getName());
                }
            }
            if (Objects.nonNull(systemAlarm.getTi())) {
                Tag tag = tagExposeService.findById(systemAlarm.getTi());
                if (Objects.nonNull(tag)) {
                    vo.setTagCode(tag.getTagCode());
                }
            }
            if (Objects.equals(systemAlarm.getTy(),Type.ASSET.getKey())) {
                Assets assets = assetsExposeService.findById(systemAlarm.getAi());
                if (Objects.nonNull(assets)) {
                    vo.setAssetsCode(assets.getCode());
                    vo.setAssetsName(assets.getName());
                }
            }
            return vo;
        }
        return null;
    }
}
