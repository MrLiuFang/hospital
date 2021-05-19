package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.LoopWashDto;
import com.lion.common.dto.RegionWashMonitorDelayDto;
import com.lion.common.utils.RedisUtil;
import com.lion.event.utils.WashRuleUtil;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/19 下午2:08
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.LOOP_WASH ,selectorExpression="*",consumerGroup = TopicConstants.LOOP_WASH_CONSUMER_GROUP)
@Log
public class LoopWashMonitorConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private WashRuleUtil washRuleUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            LoopWashDto loopWashDto = jacksonObjectMapper.readValue(msg, LoopWashDto.class);
            redisUtil.
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
