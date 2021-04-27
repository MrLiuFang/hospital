package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.event.constant.TopicConstants;
import com.lion.event.dto.AlarmDto;
import com.lion.event.dto.EventDto;
import com.lion.event.dto.RegionWashDto;
import com.lion.event.utils.MessageDelayUtil;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:18
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.ALARM_DELAY,selectorExpression="*",consumerGroup = TopicConstants.ALARM_DELAY_CONSUMER_GROUP)
@Log
public class AlarmDelayConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            AlarmDto alarmDto = jacksonObjectMapper.readValue(msg, AlarmDto.class);
            if (Objects.isNull(alarmDto.getDelayDateTime())){
                rocketMQTemplate.syncSend(TopicConstants.ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
            }else {
                Duration duration = Duration.between(LocalDateTime.now(),alarmDto.getDelayDateTime());
                long millis = duration.toMillis();
                if (millis <= 1000) {
                    log.info("推送警告命令");
                    rocketMQTemplate.syncSend(TopicConstants.ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                }
                Integer delayLevel = MessageDelayUtil.getDelayLevel(alarmDto.getDelayDateTime());
                if (delayLevel > -1) {
                    log.info("推送延迟警告命令(循环延迟)");
                    rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build(), 1000, delayLevel);
                }
            }
        }catch (Exception e){

        }
    }
}
