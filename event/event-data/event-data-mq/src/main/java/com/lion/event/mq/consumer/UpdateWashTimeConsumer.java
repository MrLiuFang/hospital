package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.UserLastWashDto;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.WashRecord;
import com.lion.event.service.WashEventService;
import com.lion.event.service.WashRecordService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/21 上午10:44
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.UPDATE_WASH_TIME,selectorExpression="*",consumerGroup = TopicConstants.UPDATE_WASH_TIME_CONSUMER_GROUP)
@Log
public class UpdateWashTimeConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private WashRecordService washRecordService;

    @Autowired
    private WashEventService washEventService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            UserLastWashDto userLastWashDto = jacksonObjectMapper.readValue(msg, UserLastWashDto.class);
            washRecordService.updateWashTime(userLastWashDto);
            washEventService.updateWashTime(userLastWashDto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
