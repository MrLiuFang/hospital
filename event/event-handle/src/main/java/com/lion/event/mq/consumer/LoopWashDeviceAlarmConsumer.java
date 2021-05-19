package com.lion.event.mq.consumer;

import com.lion.common.constants.TopicConstants;
import lombok.extern.java.Log;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/19 下午3:44
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.LOOP_WASH ,selectorExpression="*",consumerGroup = TopicConstants.LOOP_WASH_CONSUMER_GROUP)
@Log
public class LoopWashDeviceAlarmConsumer {
}
