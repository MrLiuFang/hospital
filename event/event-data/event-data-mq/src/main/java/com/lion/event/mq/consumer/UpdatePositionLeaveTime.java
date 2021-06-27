package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.UpdatePositionLeaveTimeDto;
import com.lion.event.entity.Position;
import com.lion.event.service.PositionService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午4:12
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.UPDATE_POSITION_LEAVE_TIME,selectorExpression="*",consumerGroup = TopicConstants.UPDATE_POSITION_LEAVE_TIME_CONSUMER_GROUP)
@Log
public class UpdatePositionLeaveTime implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private PositionService positionService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            UpdatePositionLeaveTimeDto dto = jacksonObjectMapper.readValue(msg, UpdatePositionLeaveTimeDto.class);
            positionService.updatePositionLeaveTime(dto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
