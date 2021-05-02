package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:06
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.EVENT,selectorExpression="*",consumerGroup = TopicConstants.EVENT_CONSUMER_GROUP)
@Log
public class EventConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private EventService eventService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Event event = jacksonObjectMapper.readValue(msg, Event.class);
            eventService.save(event);
        }catch (Exception e){

        }
    }
}
