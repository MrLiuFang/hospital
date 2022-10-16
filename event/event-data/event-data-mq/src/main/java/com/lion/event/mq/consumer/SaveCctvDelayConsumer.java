package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.UserLastWashDto;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.service.SaveCctvService;
import com.lion.event.service.WashEventService;
import com.lion.event.service.WashRecordService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = TopicConstants.SAVE_CCTV_DELAY,selectorExpression="*",consumerGroup = TopicConstants.SAVE_CCTV_DELAY_CONSUMER_GROUP)
public class SaveCctvDelayConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private WashRecordService washRecordService;

    @Autowired
    private WashEventService washEventService;

    @Autowired
    private SaveCctvService saveCctvService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            SystemAlarm systemAlarm = jacksonObjectMapper.readValue(msg, SystemAlarm.class);
            saveCctvService.saveCctv(systemAlarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
