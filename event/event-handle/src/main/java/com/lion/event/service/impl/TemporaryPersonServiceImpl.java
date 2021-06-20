package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.TemporaryPersonService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.RestrictedAreaExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 下午2:59
 */
@Service
public class TemporaryPersonServiceImpl implements TemporaryPersonService {

    @Autowired
    private CommonService commonService;

    @DubboReference
    private RestrictedAreaExposeService restrictedAreaExposeService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void temporaryPersonEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, TemporaryPerson temporaryPerson) throws JsonProcessingException {
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        commonService.position(deviceDataDto,temporaryPerson,Objects.nonNull(currentRegionDto)?currentRegionDto.getRegionId():null,tag );
        List<RestrictedArea> restrictedAreas = restrictedAreaExposeService.find(temporaryPerson.getId(), PersonType.TEMPORARY_PERSON);
        Boolean isLeaveRestrictedArea = true;
        if (Objects.isNull(restrictedAreas) || restrictedAreas.size()<=0){
            return;
        }
        for (RestrictedArea restrictedArea : restrictedAreas) {
            if (Objects.equals(restrictedArea.getRegionId(),currentRegionDto.getRegionId())) {
                isLeaveRestrictedArea = false;
            }
        }

        if (isLeaveRestrictedArea){
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(Type.MIGRANT);
            systemAlarmDto.setTagId(tag.getId());
            systemAlarmDto.setPeopleId(temporaryPerson.getId());
            systemAlarmDto.setSystemAlarmType(SystemAlarmType.CCXDFW);
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setUuid(UUID.randomUUID().toString());
            systemAlarmDto.setRegionId(currentRegionDto.getRegionId());
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        }
    }
}
