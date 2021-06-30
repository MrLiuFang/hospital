package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.SystemAlarmState;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagRuleEffect;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagRule;
import com.lion.event.service.CommonService;
import com.lion.event.service.UserButtonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 上午11:29
 */
@Service
public class UserButtonServiceImpl implements UserButtonService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void tagButtonEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, User user) throws JsonProcessingException {
        TagRule tagRule = redisUtil.getTagRule(user.getId());
        if (Objects.isNull(tagRule)) {
            return;
        }
        if (Objects.isNull(deviceDataDto.getButtonId())) {
            return;
        }
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        if (Objects.isNull(currentRegionDto)) {
            return;
        }
        commonService.position(deviceDataDto,user,currentRegionDto.getRegionId(),tag );
        if (Objects.equals(1,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getGreenButton().getDesc(),deviceDataDto.getButtonId(), tag, deviceDataDto);
            event(tagRule.getGreenButtonTip(),tagRule.getGreenButton(),tag,currentRegionDto,user);
        }else if (Objects.equals(2,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getRedButton().getDesc(),deviceDataDto.getButtonId(), tag, deviceDataDto);
            event(tagRule.getRedButtonTip(),tagRule.getRedButton(),tag,currentRegionDto,user);
        }else if (Objects.equals(3,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getYellowButton().getDesc(),deviceDataDto.getButtonId(), tag, deviceDataDto);
            event(tagRule.getYellowButtonTip(),tagRule.getYellowButton(),tag,currentRegionDto,user);
        }else if (Objects.equals(4,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getBottomButton().getDesc(),deviceDataDto.getButtonId(), tag, deviceDataDto);
            event(tagRule.getBottomButtonTip(),tagRule.getBottomButton(),tag,currentRegionDto,user);
        }
    }

    private void event(Boolean buttonTip,TagRuleEffect tagRuleEffect,Tag tag,CurrentRegionDto currentRegionDto,User user) throws JsonProcessingException {
        if (Objects.equals(buttonTip,true) && Objects.equals(tagRuleEffect, TagRuleEffect.EMPLOYEE_CALL)) {
            systemAlarm(Type.STAFF,tag,SystemAlarmType.ZDHJ,currentRegionDto);
        }
        if (Objects.equals(tagRuleEffect, TagRuleEffect.ALARM_KNOW) || Objects.equals(tagRuleEffect, TagRuleEffect.CANCEL)) {
            systemAlarmUpdateState(user,currentRegionDto,tagRuleEffect);
        }
    }

    private void systemAlarmUpdateState(User user,CurrentRegionDto currentRegionDto,TagRuleEffect tagRuleEffect) throws JsonProcessingException {
        SystemAlarmHandleDto dto = new SystemAlarmHandleDto();
        if (Objects.equals(tagRuleEffect,TagRuleEffect.CANCEL)) {
            dto.setState(SystemAlarmState.CANCEL_CALL);
        }else if (Objects.equals(tagRuleEffect,TagRuleEffect.ALARM_KNOW)) {
            dto.setState(SystemAlarmState.WELL_KNOWN);
        }
        dto.setPeopleId(user.getId());
        dto.setRegionId(currentRegionDto.getRegionId());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM_HANDLE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(dto)).build());
    }

    private void record(CurrentRegionDto currentRegionDto,User user,String buttonName,Integer buttonId,Tag tag,DeviceDataDto deviceDataDto) throws JsonProcessingException {
        UserTagButtonRecordDto userTagButtonRecordDto = new UserTagButtonRecordDto();
        userTagButtonRecordDto.setRi(currentRegionDto.getRegionId());
        userTagButtonRecordDto.setPi(user.getId());
        userTagButtonRecordDto.setBn(buttonName);
        userTagButtonRecordDto.setBi(buttonId);
        userTagButtonRecordDto.setTi(tag.getId());
        userTagButtonRecordDto.setDdt(deviceDataDto.getTime());
        userTagButtonRecordDto.setSdt(deviceDataDto.getSystemDateTime());
        rocketMQTemplate.syncSend(TopicConstants.BUTTON_RECORD, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(userTagButtonRecordDto)).build());
    }

    private void systemAlarm(Type type, Tag tag, SystemAlarmType systemAlarmType, CurrentRegionDto currentRegionDto) throws JsonProcessingException {
        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(type);
        if (Objects.nonNull(tag)){
            systemAlarmDto.setTagId(tag.getId());
        }
        systemAlarmDto.setSystemAlarmType(systemAlarmType);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setRegionId(Objects.isNull(systemAlarmDto)?null:currentRegionDto.getRegionId());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }
}
