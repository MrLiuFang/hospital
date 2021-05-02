package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.event.entity.Position;
import com.lion.event.service.PositionService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:17
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.POSITION,selectorExpression="*",consumerGroup = TopicConstants.POSITION_CONSUMER_GROUP)
@Log
public class PositionConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private PositionService positionService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Position position = jacksonObjectMapper.readValue(msg, Position.class);
            positionService.save(position);
        }catch (Exception e){

        }
    }
}
