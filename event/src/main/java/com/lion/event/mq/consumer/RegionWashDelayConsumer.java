package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.RedisConstants;
import com.lion.event.constant.TopicConstants;
import com.lion.event.dto.RegionWashDelayDto;
import com.lion.event.dto.RegionWashDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.utils.MessageDelayUtil;
import com.lion.event.utils.RedisUtil;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import lombok.SneakyThrows;
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
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午3:52
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.REGION_WASH_DELAY,selectorExpression="*",consumerGroup = TopicConstants.REGION_WASH_DELAY_CONSUMER_GROUP)
@Log
public class RegionWashDelayConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String msg = new String(body);
        RegionWashDelayDto regionWashDelayDto = jacksonObjectMapper.readValue(msg, RegionWashDelayDto.class);
        if (Objects.nonNull(regionWashDelayDto)) {
            UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+regionWashDelayDto.getUserId());
            if (Objects.nonNull(userCurrentRegionDto)){
                if (!Objects.equals(userCurrentRegionDto.getRegionId(),regionWashDelayDto.getRegionId())){
                    return;
                }else {
                    if (Objects.nonNull(regionWashDelayDto.getDelayDateTime())){
                        Duration duration = Duration.between(regionWashDelayDto.getDelayDateTime(),LocalDateTime.now());
                        long millis = duration.toMillis();
                        if (millis<=1000) {
                            RegionWashDto regionWashDto = new RegionWashDto();
                            regionWashDto.setRegionId(userCurrentRegionDto.getRegionId());
                            regionWashDto.setUserId(userCurrentRegionDto.getUserId());
                            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDto)).build());
                        }else {
                            Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashDelayDto.getDelayDateTime());
                            if (delayLevel > -1) {
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build(), 1000, delayLevel);
                            }
                        }
                        return;
                    }

                    List<Wash> washList = redisUtil.getWash(userCurrentRegionDto.getRegionId());
                    washList.forEach(wash -> {
                        if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime())) {
                            RegionWashDto regionWashDto = new RegionWashDto();
                            regionWashDto.setRegionId(userCurrentRegionDto.getRegionId());
                            regionWashDto.setUserId(userCurrentRegionDto.getUserId());
                            try {
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDto)).build());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getOvertimeRemind())) {
                            regionWashDelayDto.setDelayDateTime(LocalDateTime.now().plusMinutes(wash.getOvertimeRemind()));
                            try {
                                Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashDelayDto.getDelayDateTime());
                                if (delayLevel > -1) {
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build(), 2000, delayLevel);
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }else if (Objects.equals(wash.getType(), WashRuleType.LOOP)){
                            regionWashDelayDto.setDelayDateTime(LocalDateTime.now().plusMinutes(wash.getInterval()));
                            try {
                                Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashDelayDto.getDelayDateTime());
                                if (delayLevel > -1) {
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build(), 2000, delayLevel);
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}
