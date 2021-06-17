package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.Type;
import com.lion.common.enums.WashEventType;
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
            String uuid = (String) redisTemplate.opsForValue().get(RedisConstants.USER_WORK_STATE_UUID+loopWashDto.getUserId());
            if (Objects.isNull(state) || !Objects.equals(state,RedisConstants.USER_WORK_STATE_START) || !Objects.equals(uuid,loopWashDto.getUuid())) {
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
                if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.nonNull(wash.getInterval()) && wash.getInterval()>1) {
                    if (Objects.isNull(loopWashDto.getMonitorDelayDateTime())) {
                        loopWashDto.setMonitorDelayDateTime(loopWashDto.getStartWorkDateTime().plusMinutes(wash.getInterval()));
                        loopWashDto.setStartWashDateTime(loopWashDto.getStartWorkDateTime().plusMinutes(wash.getInterval()-1));
                        loopWashDto.setEndWashDateTime(loopWashDto.getStartWorkDateTime().plusMinutes(wash.getInterval()));
                    }
                    Duration duration = Duration.between(LocalDateTime.now(), loopWashDto.getMonitorDelayDateTime());
                    long millis = duration.toMillis();
                    if (millis<1000){
                        loopWashDto.setCount(loopWashDto.getCount()+1);
                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+loopWashDto.getUserId());
                        if (Objects.isNull(userLastWashDto)) {
                            // 记录洗手事件 (错过洗手)
                            try {
                                recordWashEvent(loopWashDto.getUserId(),null, SystemAlarmType.ZZDQYWJXXSCZ,loopWashDto, wash,userLastWashDto);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }else {
                            //判断洗手是否在规定时间范围内
                            if (!(userLastWashDto.getDateTime().isAfter(loopWashDto.getStartWashDateTime()) && userLastWashDto.getDateTime().isBefore(loopWashDto.getEndWashDateTime()))) {
                                // 记录洗手事件 (错过洗手)
                                try {
                                    recordWashEvent(loopWashDto.getUserId(),null, SystemAlarmType.ZZDQYWJXXSCZ,loopWashDto, wash,userLastWashDto);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                //判断是否在规定的洗手设备类型洗手
                                Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(),wash);
                                if (!b){
                                    try {
                                        recordWashEvent(loopWashDto.getUserId(),userLastWashDto.getDateTime(), SystemAlarmType.WXYBZDXSSBXS,loopWashDto, wash,userLastWashDto);
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    try {
                                        recordWashEvent(loopWashDto.getUserId(),userLastWashDto.getDateTime(), loopWashDto, wash,userLastWashDto);
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        loopWashDto.setMonitorDelayDateTime(LocalDateTime.now().plusMinutes(wash.getInterval()));
                        loopWashDto.setStartWashDateTime(loopWashDto.getMonitorDelayDateTime().plusMinutes(wash.getInterval()-1));
                        loopWashDto.setEndWashDateTime(loopWashDto.getMonitorDelayDateTime().plusMinutes(wash.getInterval()));
                    }
                    delay(loopWashDto);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void delay(LoopWashDto loopWashDto){
        Integer delayLevel = MessageDelayUtil.getDelayLevel(loopWashDto.getMonitorDelayDateTime());
        if (delayLevel > -1) {
            try {
                rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDto)).build(), 1000, delayLevel);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private void recordWashEvent(Long userId,LocalDateTime wt,LoopWashDto loopWashDto,Wash wash,UserLastWashDto userLastWashDto) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + userId);
        WashRecordDto washRecordDto = washCommon.init(userId,Objects.nonNull(userCurrentRegionDto)?userCurrentRegionDto.getRegionId():null,null,null , null,null);
        WashEventDto washEventDto = new WashEventDto();
        washEventDto.setIa(false);
        washEventDto.setWet(WashEventType.LOOP.getKey());
        washEventDto.setWt(null);
        BeanUtils.copyProperties(washRecordDto,washEventDto);
        rocketMQTemplate.syncSend(TopicConstants.WASH_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(washEventDto)).build());

    }

    private void recordWashEvent(Long userId,LocalDateTime wt,SystemAlarmType systemAlarmType,LoopWashDto loopWashDto,Wash wash,UserLastWashDto userLastWashDto) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + userId);
        WashRecordDto washRecordDto = washCommon.init(userId,Objects.nonNull(userCurrentRegionDto)?userCurrentRegionDto.getRegionId():null,null,null , null,null);
        WashEventDto washEventDto = new WashEventDto();
        washEventDto.setIa(true);
        washEventDto.setWet(WashEventType.LOOP.getKey());
        washEventDto.setAt(systemAlarmType.getKey());
        if (Objects.nonNull(wt)){
            washEventDto.setWt(wt);
        }
        BeanUtils.copyProperties(washRecordDto,washEventDto);
        rocketMQTemplate.syncSend(TopicConstants.WASH_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(washEventDto)).build());

        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(Type.STAFF);
        systemAlarmDto.setTagId(userLastWashDto.getTagId());
        systemAlarmDto.setRegionId(Objects.nonNull(userCurrentRegionDto)?userCurrentRegionDto.getRegionId():null);
        systemAlarmDto.setPeopleId(userId);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setSystemAlarmType(systemAlarmType);
        systemAlarmDto.setUuid(loopWashDto.getUuid());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());

        //给硬件发送数据
        LoopWashDeviceAlarmDto loopWashDeviceAlarmDto = new LoopWashDeviceAlarmDto();
        loopWashDeviceAlarmDto.setUserId(userId);
        loopWashDeviceAlarmDto.setUuid(loopWashDto.getUuid());
        loopWashDeviceAlarmDto.setDeviceDelayAlarmDateTime(LocalDateTime.now());
        loopWashDeviceAlarmDto.setStartAlarmDateTime(loopWashDeviceAlarmDto.getDeviceDelayAlarmDateTime());
        loopWashDeviceAlarmDto.setEndAlarmDateTime(loopWashDeviceAlarmDto.getDeviceDelayAlarmDateTime().plusMinutes(wash.getInterval()));
        rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH_DEVICE_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDeviceAlarmDto)).build());
    }
}
