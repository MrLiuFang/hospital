package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.dao.SystemAlarmDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmVo;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
    public void unalarm(String uuid, String id) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(userId)) {
            User user = userExposeService.findById(userId);
            if (Objects.nonNull(user)) {
                alarmDao.unalarm(uuid, id, userId, user.getName());
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
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return alarmDao.list(lionPage,departmentIds,ua,startDateTime,endDateTime);
    }
}
