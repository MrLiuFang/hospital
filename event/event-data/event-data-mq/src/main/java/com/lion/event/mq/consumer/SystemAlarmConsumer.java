package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

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


    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            SystemAlarmDto systemAlarmDto = jacksonObjectMapper.readValue(msg, SystemAlarmDto.class);

            if (systemAlarmDto.getDelayDateTime().isAfter(LocalDateTime.now())){
                againAlarm(systemAlarmDto);
                return;
            }else {
                Alarm alarm = getAlarm(systemAlarmDto);
                if (systemAlarmDto.getCount()>1){
                    systemAlarmDto.setCount(systemAlarmDto.getCount()+1);
                    systemAlarmService.updateSdt(systemAlarmDto.getUuid());
                }else {
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
                    systemAlarm.setUuid(systemAlarmDto.getUuid());
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
        if (Objects.equals(systemAlarmDto.getType(), Type.STAFF.getKey())){
            alarm = redisUtil.getAlarm(AlarmClassify.STAFF, systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.TEMPERATURE.getKey()) || Objects.equals(systemAlarmDto.getType(), Type.HUMIDITY.getKey())){
            alarm = redisUtil.getAlarm(AlarmClassify.TEMPERATURE_HUMIDITY_INSTRUMENT,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.ASSET.getKey())){
            alarm = redisUtil.getAlarm(AlarmClassify.ASSETS,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.PATIENT.getKey())){
            // TODO: 2021/5/17 获取患者级别
            alarm = redisUtil.getAlarm(AlarmClassify.PATIENT,systemAlarmDto.getSystemAlarmType(),null);
        }
        else if (Objects.equals(systemAlarmDto.getType(), Type.DEVICE.getKey())){
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
