package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.Type;
import com.lion.common.utils.MessageDelayUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.event.mq.consumer.common.WashCommon;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private WashCommon washCommon;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            LoopWashDto loopWashDto = jacksonObjectMapper.readValue(msg, LoopWashDto.class);
            String state = (String) redisTemplate.opsForValue().get(RedisConstants.USER_WORK_STATE+loopWashDto.getUserId());
            if (Objects.isNull(state) || Objects.equals(RedisConstants.USER_WORK_STATE_START,state)) {
                return;
            }
            List<Wash> list = redisUtil.getLoopWash();
            if (Objects.isNull(list) || list.size()<=0) {
                if (Objects.nonNull(loopWashDto.getUserId())) {
                    list = redisUtil.getLoopWashByUserId(loopWashDto.getUserId());
                }
            }
            if (Objects.nonNull(list) && list.size()<=0) {
                return;
            }
            list.forEach(wash -> {
                if (Objects.equals(wash.getType(), WashRuleType.LOOP)) {
                    if (Objects.isNull(loopWashDto.getMonitorDelayDateTime())) {
                        loopWashDto.setMonitorDelayDateTime(loopWashDto.getStartWashDateTime().plusMinutes(wash.getInterval()));
                        loopWashDto.setStartWashDateTime(loopWashDto.getStartWashDateTime().plusMinutes(wash.getInterval()-1));
                        loopWashDto.setEndWashDateTime(loopWashDto.getStartWashDateTime().plusMinutes(wash.getInterval()));
                    }else {
                        loopWashDto.setMonitorDelayDateTime(loopWashDto.getMonitorDelayDateTime().plusMinutes(wash.getInterval()));
                        loopWashDto.setStartWashDateTime(loopWashDto.getMonitorDelayDateTime().plusMinutes(wash.getInterval()-1));
                        loopWashDto.setEndWashDateTime(loopWashDto.getMonitorDelayDateTime().plusMinutes(wash.getInterval()));
                    }
                    Duration duration = Duration.between(LocalDateTime.now(), loopWashDto.getMonitorDelayDateTime());
                    long millis = duration.toMillis();
                    if (millis<1000){
                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+loopWashDto.getUserId());
                        if (Objects.isNull(userLastWashDto)) {
                            // 记录洗手事件 (错过洗手)
                            try {
                                recordWashEvent(loopWashDto.getUserId());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }else {
                            //判断洗手是否在规定时间范围内
                            if (!(userLastWashDto.getDateTime().isAfter(loopWashDto.getStartWashDateTime()) && userLastWashDto.getDateTime().isBefore(loopWashDto.getEndWashDateTime()))) {
                                // 记录洗手事件 (错过洗手)
                                try {
                                    recordWashEvent(loopWashDto.getUserId());
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else {
                        Integer delayLevel = MessageDelayUtil.getDelayLevel(loopWashDto.getMonitorDelayDateTime());
                        if (delayLevel > -1) {
                            try {
                                rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDto)).build(), 1000, delayLevel);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recordWashEvent(Long userId) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + userId);
        WashRecordDto washRecordDto = washCommon.init(userId,Objects.nonNull(userCurrentRegionDto)?userCurrentRegionDto.getRegionId():null,null,null , null,null);
        WashEventDto washEventDto = new WashEventDto();
        BeanUtils.copyProperties(washRecordDto,washEventDto);
        rocketMQTemplate.syncSend(TopicConstants.WASH_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(washEventDto)).build());
        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(Type.STAFF);
        systemAlarmDto.setPeopleId(userId);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setSystemAlarmType(SystemAlarmType.ZZDQYWJXXSCZ);
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }
}
