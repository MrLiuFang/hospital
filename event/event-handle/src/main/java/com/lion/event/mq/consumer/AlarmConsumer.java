package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.RedisUtil;
import com.lion.event.dto.AlarmDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.enums.AlarmType;
import com.lion.event.utils.MessageDelayUtil;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:18
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.ALARM,selectorExpression="*",consumerGroup = TopicConstants.ALARM_CONSUMER_GROUP)
@Log
public class AlarmConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            AlarmDto alarmDto = jacksonObjectMapper.readValue(msg, AlarmDto.class);
            User user = redisUtil.getUserById(alarmDto.getUserId());
            if (Objects.nonNull(user) && Objects.equals(alarmDto.getAlarmType(), AlarmType.REGION_WASH_ALARM)){
                washAlarm(user,alarmDto);
            }

        }catch (Exception exception){

        }
    }

    private void washAlarm(User user,AlarmDto alarmDto) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        if (!Objects.equals(userCurrentRegionDto.getRegionId(),alarmDto.getRegionId())){
            //如果用户从需要警告的区域离开则解除警告
            log.info(user.getName()+"->离开之前的区域,解除警告");
            return;
        }
        Alarm alarm = redisUtil.getAlarm(AlarmClassify.STAFF);
        if (Objects.isNull(alarm)){
            return;
        }
        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
        if (Objects.nonNull(userLastWashDto) && Objects.nonNull(userLastWashDto.getDateTime()) && userLastWashDto.getDateTime().isAfter(alarmDto.getAlarmDateTime())){
            //解除警告
            log.info(user.getName()+"->解除警告");
            return;
        }
        log.info(user.getName()+"->发送洗手警告");
        if (Objects.equals(true,alarm.getAgain())){
            alarmDto.setDelayDateTime(LocalDateTime.now().plusMinutes(alarm.getInterval()));
        }
        Integer delayLevel = MessageDelayUtil.getDelayLevel(alarmDto.getDelayDateTime());
        if (delayLevel > -1) {
//            log.info("推送延迟警告命令");
            rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build(), 1000, delayLevel);
        }
    }
}
