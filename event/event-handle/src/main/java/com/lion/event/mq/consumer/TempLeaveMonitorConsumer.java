package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.RegionWashMonitorDelayDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.TempLeaveMonitorDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.MessageDelayUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.expose.person.RestrictedAreaExposeService;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
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
import java.util.UUID;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/8 上午10:50
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.TEMP_LEAVE_MONITOR,selectorExpression="*",consumerGroup = TopicConstants.TEMP_LEAVE_MONITOR_CONSUMER_GROUP)
@Log
public class TempLeaveMonitorConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @DubboReference
    private RestrictedAreaExposeService restrictedAreaExposeService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {

            byte[] body = messageExt.getBody();
            String msg = new String(body);
            TempLeaveMonitorDto tempLeaveMonitorDto = jacksonObjectMapper.readValue(msg, TempLeaveMonitorDto.class);
            if (Objects.nonNull(tempLeaveMonitorDto) && Objects.nonNull(tempLeaveMonitorDto.getDelayDateTime())) {
                Duration duration = Duration.between(LocalDateTime.now(), tempLeaveMonitorDto.getDelayDateTime());
                long millis = duration.toMillis();
                if (millis <= 1000) {
                    CurrentRegionDto currentRegionDto = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.PATIENT_CURRENT_REGION+tempLeaveMonitorDto.getPatientId());
                    if ( Objects.nonNull(currentRegionDto)) {
                        List<RestrictedArea> restrictedAreas = restrictedAreaExposeService.find(tempLeaveMonitorDto.getPatientId(), PersonType.PATIENT );
                        for (RestrictedArea restrictedArea : restrictedAreas) {
                            if (Objects.equals(restrictedArea.getRegionId(),currentRegionDto.getRegionId())) {
                                return;
                            }
                        }
                    }
                    SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
                    systemAlarmDto.setDateTime(LocalDateTime.now());
                    systemAlarmDto.setType(Type.PATIENT);
                    systemAlarmDto.setTagId(null);
                    systemAlarmDto.setPeopleId(tempLeaveMonitorDto.getPatientId());
                    systemAlarmDto.setSystemAlarmType(SystemAlarmType.CCXDFW);
                    systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
                    systemAlarmDto.setRegionId(currentRegionDto.getRegionId());
                    rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                } else {
                    Integer delayLevel = MessageDelayUtil.getDelayLevel(tempLeaveMonitorDto.getDelayDateTime());
                    if (delayLevel > -1) {
                        rocketMQTemplate.syncSend(TopicConstants.TEMP_LEAVE_MONITOR, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(tempLeaveMonitorDto)).build(), 1000, delayLevel);
                    }
                }
                return;
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
