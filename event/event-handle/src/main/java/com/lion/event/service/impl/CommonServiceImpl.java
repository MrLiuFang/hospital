package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.ExposeObject;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午2:43
 **/
@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommonService commonService;


    @Override
    public void position(DeviceDataDto deviceDataDto, User user, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.STAFF,user.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime(), deviceDataDto.getMonitorId());
        penaltyZoneAlarm(Type.STAFF,user.getId(),regionId,tag.getId());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Patient patient, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.PATIENT,patient.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime(),deviceDataDto.getMonitorId() );
        CurrentRegionDto currentRegionDto = commonService.currentRegion(deviceDataDto);
        if (Objects.nonNull(currentRegionDto)) {
            CurrentRegionDto lastCurrentRegionDto = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.LAST_REGION + patient.getId());
            if (Objects.nonNull(lastCurrentRegionDto) && !Objects.equals(currentRegionDto.getRegionId(), lastCurrentRegionDto.getRegionId())){
                if (Objects.nonNull(lastCurrentRegionDto.getTime()) && Objects.nonNull(lastCurrentRegionDto.getSystemDateTime())) {
                    UpdatePositionLeaveTimeDto dto = new UpdatePositionLeaveTimeDto();
                    dto.setPi(patient.getId());
                    dto.setPddt(lastCurrentRegionDto.getTime());
                    dto.setPsdt(lastCurrentRegionDto.getSystemDateTime());
                    dto.setCddt(deviceDataDto.getTime());
                    dto.setCsdt(deviceDataDto.getSystemDateTime());
                    try {
                        rocketMQTemplate.syncSend(TopicConstants.UPDATE_POSITION_LEAVE_TIME, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(dto)).build());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            currentRegionDto.setTime(deviceDataDto.getTime());
            currentRegionDto.setSystemDateTime(deviceDataDto.getSystemDateTime());
            redisTemplate.opsForValue().set(RedisConstants.LAST_REGION + patient.getId(), currentRegionDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
        penaltyZoneAlarm(Type.PATIENT,patient.getId(),regionId,tag.getId());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, TemporaryPerson temporaryPerson, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.MIGRANT,temporaryPerson.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime(), deviceDataDto.getMonitorId());
        penaltyZoneAlarm(Type.MIGRANT,temporaryPerson.getId(),regionId,tag.getId());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Tag tag, Long regionId) throws JsonProcessingException {
        if (Objects.equals(deviceDataDto.getTagType(),Type.HUMIDITY)) {
            position(Type.HUMIDITY, null, regionId, null, tag.getId(), deviceDataDto.getTime(), deviceDataDto.getSystemDateTime(), deviceDataDto.getMonitorId());
        }else if (Objects.equals(deviceDataDto.getTagType(),Type.TEMPERATURE)) {
            position(Type.TEMPERATURE, null, regionId, null, Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(), deviceDataDto.getSystemDateTime(), deviceDataDto.getMonitorId());
        }
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Assets assets, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.ASSET,null,regionId,assets.getId(),Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime(), deviceDataDto.getMonitorId());
    }

    @Override
    public CurrentRegionDto currentRegion(Device monitor, Device star) {
        Region monitorRegion = null;
        Region starRegion = null;
        if (Objects.nonNull(monitor) && Objects.nonNull(monitor.getId())) {
            monitorRegion = redisUtil.getRegion(monitor.getId());
        }
        if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
            starRegion = redisUtil.getRegion(star.getId());
        }
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        if (Objects.isNull(region)){
            return null;
        }
        CurrentRegionDto currentRegionDto = new CurrentRegionDto();
        currentRegionDto.setRegionId(region.getId());
        return currentRegionDto;
    }

    @Override
    public CurrentRegionDto currentRegion(DeviceDataDto deviceDataDto) {
        Device monitor = null;
        Device star = null;
        if (Objects.nonNull(deviceDataDto.getMonitorId())) {
            monitor = redisUtil.getDevice(deviceDataDto.getMonitorId());
        }
        if (Objects.nonNull(deviceDataDto.getStarId())) {
            star = redisUtil.getDevice(deviceDataDto.getStarId());
        }
        return currentRegion(monitor,star);
    }

    /**
     * 判断是否进入禁区
     * @param type
     * @param pi
     * @param ri
     * @param ti
     */
    private void penaltyZoneAlarm(Type type,Long pi,Long ri, Long ti) {
        Region region = redisUtil.getRegionById(ri);
        if (Objects.isNull(region)) {
            return;
        }
        if (Objects.equals(region.isPublic,true)) {
            List<ExposeObject> exposeObjects = redisTemplate.opsForList().range(RedisConstants.REGION_EXPOSE_OBJECT+region.getId(),0,-1);
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(type);
            systemAlarmDto.setTagId(ti);
            systemAlarmDto.setPeopleId(pi);
            systemAlarmDto.setSystemAlarmType(SystemAlarmType.JRJQ);
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setRegionId(ri);
            if (Objects.equals(type,Type.STAFF)) {
                if (!exposeObjects.contains(ExposeObject.STAFF)) {
                    try {
                        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (Objects.equals(type,Type.PATIENT)) {
                if (!exposeObjects.contains(ExposeObject.PATIENT)) {
                    try {
                        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (Objects.equals(type,Type.MIGRANT)) {
                if (!exposeObjects.contains(ExposeObject.POSTDOCS)) {
                    try {
                        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     *
     * @param type
     * @param pi 员工/患者/流动人员id
     * @param ri 区域id
     * @param adi 设备/资产id(资产)
     * @param ti 标签id(温度)
     * @param ddt 设备产生的时间
     * @param sdt 系统接收到的时间
     * @param monitorId
     * @throws JsonProcessingException
     */
    private void position(Type type, Long pi, Long ri, Long adi, Long ti, LocalDateTime ddt, LocalDateTime sdt,String monitorId) throws JsonProcessingException {
        if (Objects.isNull(ri)){
            return;
        }
        PositionDto positionDto = new PositionDto();
        positionDto.setTyp(type.getKey());
        if (Objects.nonNull(pi)) {
            positionDto.setPi(pi);
        }
        if (Objects.nonNull(ri)) {
            positionDto.setRi(ri);
        }
        if (Objects.nonNull(adi)) {
            positionDto.setAdi(adi);
        }
        if (Objects.nonNull(ti)) {
            positionDto.setTi(ti);
        }
        Device device = redisUtil.getDevice(monitorId);
        if (Objects.nonNull(device)) {
            if (StringUtils.hasText(device.getX())){
                positionDto.setX(device.getX());
            }
            if (StringUtils.hasText(device.getY())){
                positionDto.setY(device.getY());
            }
        }
        positionDto.setDdt(ddt);
        positionDto.setSdt(sdt);
        rocketMQTemplate.syncSend(TopicConstants.POSITION, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(positionDto)).build());

    }
}
