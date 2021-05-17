package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.TagEvent;
import com.lion.event.entity.TagRecord;
import com.lion.event.mq.consumer.utils.TagCommonUtil;
import com.lion.event.service.SystemAlarmService;
import com.lion.event.service.TagEventService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午7:57
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.TAG_EVENT,selectorExpression="*",consumerGroup = TopicConstants.TAG_EVENT_CONSUMER_GROUP)
@Log
public class TagEventConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private TagCommonUtil tagCommonUtil;

    @Autowired
    private TagEventService tagEventService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            TagEvent tagEvent = jacksonObjectMapper.readValue(msg, TagEvent.class);
            tagEvent = (TagEvent) tagCommonUtil.setRegionInfo(tagEvent);
            tagEventService.save(tagEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
