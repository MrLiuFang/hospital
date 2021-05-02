package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.event.entity.Alarm;
import com.lion.event.entity.Event;
import com.lion.event.service.AlarmService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:28
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.ALARM_TO_STORAGE,selectorExpression="*",consumerGroup = TopicConstants.ALARM_TO_STORAGE_CONSUMER_GROUP)
@Log
public class AlarmConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private AlarmService alarmService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Alarm alarm = jacksonObjectMapper.readValue(msg, Alarm.class);
            alarmService.save(alarm);
        }catch (Exception e){

        }
    }
}
