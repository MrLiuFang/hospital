package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.UserTagButtonRecordDto;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagRule;
import com.lion.event.service.CommonService;
import com.lion.event.service.UserButtonService;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        commonService.position(deviceDataDto,user,currentRegionDto.getRegionId(),tag );
        if (Objects.equals(1,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getGreenButton().getDesc(),deviceDataDto.getButtonId());
        }else if (Objects.equals(2,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getRedButton().getDesc(),deviceDataDto.getButtonId());
        }else if (Objects.equals(3,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getYellowButton().getDesc(),deviceDataDto.getButtonId());
        }else if (Objects.equals(4,deviceDataDto.getButtonId())) {
            record(currentRegionDto,user,tagRule.getBottomButton().getDesc(),deviceDataDto.getButtonId());
        }
    }

    private void record(CurrentRegionDto currentRegionDto,User user,String buttonName,Integer buttonId) throws JsonProcessingException {
        UserTagButtonRecordDto userTagButtonRecordDto = new UserTagButtonRecordDto();
        userTagButtonRecordDto.setRi(currentRegionDto.getRegionId());
        userTagButtonRecordDto.setPi(user.getId());
        userTagButtonRecordDto.setBn(buttonName);
        userTagButtonRecordDto.setBi(buttonId);
        rocketMQTemplate.syncSend(TopicConstants.BUTTON_RECORD, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(userTagButtonRecordDto)).build());
    }

}
