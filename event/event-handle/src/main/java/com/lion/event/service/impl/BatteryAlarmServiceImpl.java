package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.BatteryAlarmService;
import com.lion.event.service.CommonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.codehaus.jackson.map.ser.std.ObjectArraySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/20 上午10:28
 */
@Service
public class BatteryAlarmServiceImpl implements BatteryAlarmService {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommonService commonService;

    @Override
    public void deviceLowBatteryAlarm(Device device, DeviceDataDto deviceDataDto) {
        systemAlarm(Type.DEVICE,null,null,Objects.nonNull(device)?device.getId():null ,null, deviceDataDto);
    }

    @Override
    public void assetsLowBatteryAlarm(Assets assets, DeviceDataDto deviceDataDto, Tag tag) {
        systemAlarm(Type.ASSET,Objects.nonNull(tag)?tag.getId():null,null,null , Objects.nonNull(assets)?assets.getId():null, deviceDataDto);
    }

    @Override
    public void userLowBatteryAlarm(User user, DeviceDataDto deviceDataDto, Tag tag) {
        systemAlarm(Type.STAFF,Objects.nonNull(tag)?tag.getId():null,Objects.nonNull(user)?user.getId():null,null , null, deviceDataDto);
    }

    @Override
    public void patientLowBatteryAlarm(Patient patient, DeviceDataDto deviceDataDto, Tag tag) {
        systemAlarm(Type.PATIENT,Objects.nonNull(tag)?tag.getId():null,Objects.nonNull(patient)?patient.getId():null,null , null, deviceDataDto);
    }

    @Override
    public void temporaryPersonLowBatteryAlarm(TemporaryPerson temporaryPerson, DeviceDataDto deviceDataDto, Tag tag) {
        systemAlarm(Type.MIGRANT,Objects.nonNull(tag)?tag.getId():null,Objects.nonNull(temporaryPerson)?temporaryPerson.getId():null,null , null, deviceDataDto);
    }

    @Override
    public void tagLowBatteryAlarm(DeviceDataDto deviceDataDto, Tag tag) {
//        if (Objects.nonNull(deviceDataDto.getHumidity()) && Objects.nonNull(deviceDataDto.getTemperature())) {
//            systemAlarm(Type., Objects.nonNull(tag) ? tag.getId() : null, null, null, null, deviceDataDto);
//            return;
//        }
        if (Objects.nonNull(deviceDataDto.getHumidity())) {
            systemAlarm(Type.HUMIDITY, Objects.nonNull(tag) ? tag.getId() : null, null, null, null, deviceDataDto);
            return;
        }
        if (Objects.nonNull(deviceDataDto.getTemperature())) {
            systemAlarm(Type.TEMPERATURE, Objects.nonNull(tag) ? tag.getId() : null, null, null, null, deviceDataDto);
            return;
        }
    }

    private void systemAlarm(Type type,Long tagId,Long peopleId,Long deviceId,Long assetsId,DeviceDataDto deviceDataDto)  {
        CurrentRegionDto currentRegionDto = commonService.currentRegion(deviceDataDto);
        if (Objects.isNull(currentRegionDto)){
            return;
        }
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setDeviceId(deviceId);
        systemAlarmDto.setAssetsId(assetsId);
        systemAlarmDto.setType(type);
        systemAlarmDto.setTagId(tagId);
        systemAlarmDto.setPeopleId(peopleId);
        systemAlarmDto.setSystemAlarmType(SystemAlarmType.BQDCBZ);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setRegionId(currentRegionDto.getRegionId());
        try {
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
