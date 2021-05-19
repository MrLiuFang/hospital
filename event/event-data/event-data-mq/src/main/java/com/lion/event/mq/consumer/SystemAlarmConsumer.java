package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.MessageDelayUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.rule.Alarm;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:28
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.SYSTEM_ALARM,selectorExpression="*",consumerGroup = TopicConstants.SYSTEM_ALARM_CONSUMER_GROUP)
@Log
public class SystemAlarmConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            SystemAlarmDto systemAlarmDto = jacksonObjectMapper.readValue(msg, SystemAlarmDto.class);
            if (Objects.isNull(systemAlarmDto.getUuid())) {
                return;
            }
            Boolean b = (Boolean) redisTemplate.opsForValue().get(RedisConstants.UNALARM+systemAlarmDto.getUuid());
            if (Objects.equals(b,true)){
                log.info("系统内解除警告");
                systemAlarmService.unalarm(systemAlarmDto.getUuid());
                redisTemplate.delete(RedisConstants.UNALARM+systemAlarmDto.getUuid());
                return;
            }
            Duration duration = Duration.between(LocalDateTime.now(),systemAlarmDto.getDelayDateTime());
            if (systemAlarmDto.getDelayDateTime().isAfter(LocalDateTime.now()) && duration.toMillis()>1000 ){
                againAlarm(systemAlarmDto);
                return;
            }else {
                Alarm alarm = getAlarm(systemAlarmDto);
                if (systemAlarmDto.getCount()>1){
                    systemAlarmDto.setCount(systemAlarmDto.getCount()+1);
                    systemAlarmService.updateSdt(systemAlarmDto.getUuid());
                    log.info("系统内触发警告");
                }else {
                    log.info("系统内触发警告");
                    SystemAlarm systemAlarm = new SystemAlarm();
                    systemAlarm.setAi(systemAlarmDto.getAssetsId());
                    systemAlarm.setDi(systemAlarmDto.getDeviceId());
                    systemAlarm.setPi(systemAlarmDto.getPeopleId());
                    if (Objects.nonNull(systemAlarmDto.getSystemAlarmType())) {
                        systemAlarm.setSat(systemAlarmDto.getSystemAlarmType().getKey());
                    }
                    systemAlarm.setTi(systemAlarmDto.getTagId());
                    if (Objects.nonNull(systemAlarmDto.getType())) {
                        systemAlarm.setTy(systemAlarmDto.getType().getKey());
                    }
                    systemAlarm.setUa(false);
                    systemAlarm.setUi(systemAlarmDto.getUuid());
                    systemAlarm.setDt(systemAlarmDto.getDateTime());
                    systemAlarm.setSdt(systemAlarmDto.getDateTime());
                    if (Objects.nonNull(alarm)) {
                        systemAlarm.setAli(alarm.getId());
                    }
                    systemAlarmService.save(systemAlarm);
                    systemAlarmDto.setCount(systemAlarmDto.getCount() + 1);
                }
                if (Objects.nonNull(alarm) && Objects.equals(true, alarm.getAgain())) {
                    systemAlarmDto.setDelayDateTime(LocalDateTime.now().plusMinutes(alarm.getInterval()));
                    againAlarm(systemAlarmDto);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Alarm getAlarm(SystemAlarmDto systemAlarmDto){
        Alarm alarm = null;
        if (Objects.equals(systemAlarmDto.getType(), Type.STAFF)){
            alarm = redisUtil.getAlarm(AlarmClassify.STAFF, systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.TEMPERATURE) || Objects.equals(systemAlarmDto.getType(), Type.HUMIDITY)){
            alarm = redisUtil.getAlarm(AlarmClassify.TEMPERATURE_HUMIDITY_INSTRUMENT,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.ASSET)){
            alarm = redisUtil.getAlarm(AlarmClassify.ASSETS,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.PATIENT)){
            // TODO: 2021/5/17 获取患者级别
            alarm = redisUtil.getAlarm(AlarmClassify.PATIENT,systemAlarmDto.getSystemAlarmType(),null);
        }
        else if (Objects.equals(systemAlarmDto.getType(), Type.DEVICE)){
            alarm = redisUtil.getAlarm(AlarmClassify.DEVICE,systemAlarmDto.getSystemAlarmType(),null);
        }
        return alarm;
    }

    private void againAlarm(SystemAlarmDto systemAlarmDto) throws JsonProcessingException {
        Integer delayLevel = MessageDelayUtil.getDelayLevel(systemAlarmDto.getDelayDateTime());
        if (delayLevel > -1) {
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build(), 1000, delayLevel);
        }
    }
}
