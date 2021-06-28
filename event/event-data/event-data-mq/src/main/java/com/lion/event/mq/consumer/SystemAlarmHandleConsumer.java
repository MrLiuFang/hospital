package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.SystemAlarmHandleDto;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.service.SystemAlarmService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午8:11
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.SYSTEM_ALARM_HANDLE,selectorExpression="*",consumerGroup = TopicConstants.SYSTEM_ALARM_HANDLE_CONSUMER_GROUP)
@Log
public class SystemAlarmHandleConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            SystemAlarmHandleDto systemAlarmDto = jacksonObjectMapper.readValue(msg, SystemAlarmHandleDto.class);
            systemAlarmService.updateState(systemAlarmDto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
