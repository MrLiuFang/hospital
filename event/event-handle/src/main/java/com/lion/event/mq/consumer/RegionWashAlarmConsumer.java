package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.AlarmDto;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.AlarmType;
import com.lion.common.enums.Type;
import com.lion.common.enums.UnalarmType;
import com.lion.common.utils.RedisUtil;
import com.lion.event.utils.MessageDelayUtil;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.Wash;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:18
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.REGION_WASH_ALARM,selectorExpression="*",consumerGroup = TopicConstants.REGION_WASH_ALARM_CONSUMER_GROUP)
@Log
public class RegionWashAlarmConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WashRuleUtil washRuleUtil;

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
            exception.printStackTrace();
        }
    }

    private void washAlarm(User user,AlarmDto alarmDto) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        if (Objects.isNull(userCurrentRegionDto)){
            return;
        }
        if (!Objects.equals(userCurrentRegionDto.getRegionId(),alarmDto.getRegionId())){
            //如果用户从需要警告的区域离开则解除警告
            unalarm(alarmDto,UnalarmType.LEAVE_REGION);
            log.info(user.getName()+"->离开之前的区域,解除警告");
            return;
        }
        Alarm alarm = redisUtil.getAlarm(AlarmClassify.STAFF);
        if (Objects.isNull(alarm)){
            unalarm(alarmDto,UnalarmType.NO_WASH_RULE);
            return;
        }
        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
        if (Objects.nonNull(userLastWashDto) && Objects.nonNull(userLastWashDto.getDateTime()) && userLastWashDto.getDateTime().isAfter(alarmDto.getAlarmDateTime())){
            List<Wash> washList = redisUtil.getWash(alarmDto.getRegionId());
            for (Wash wash :washList){
                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(), wash);
                if (Objects.equals(false, b)) {
                    log.info("->发送洗手警告");
                    try {
                        again(alarmDto,alarm);
                        storageAlarm(alarmDto,alarm);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            };

            //解除警告
            log.info(user.getName()+"->解除警告");
            unalarm(alarmDto,UnalarmType.WASH);
            return;
        }
        log.info(user.getName()+"->发送洗手警告");
        again(alarmDto,alarm);
        storageAlarm(alarmDto,alarm);
    }

    private void again(AlarmDto alarmDto,Alarm alarm) throws JsonProcessingException {
        if (Objects.equals(true,alarm.getAgain())){
            alarmDto.setDelayDateTime(LocalDateTime.now().plusMinutes(alarm.getInterval()));
        }
        Integer delayLevel = MessageDelayUtil.getDelayLevel(alarmDto.getDelayDateTime());
        if (delayLevel > -1) {
//            log.info("推送延迟警告命令");
            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build(), 1000, delayLevel);
        }
    }

    /**
     * 保存警告数据
     * @param alarmDto
     * @param alarm
     */
    private void storageAlarm(AlarmDto alarmDto,Alarm alarm) throws JsonProcessingException {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("typ", Type.STAFF); //警告类型
        map.put("pi", alarmDto.getUserId()); //员工id
        map.put("ai", alarm.getId()); //警告id
        map.put("an", alarm.getContent()); //警告名称
        map.put("sdt",LocalDateTime.now()); //警告时间
        map.put("uuid",alarmDto.getUuid()); //事件唯一标识
        rocketMQTemplate.syncSend(TopicConstants.ALARM_TO_STORAGE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(map)).build());
    }

    /**
     * 解除警告
     * @param alarmDto
     * @param unalarmType
     */
    private void unalarm(AlarmDto alarmDto, UnalarmType unalarmType) throws JsonProcessingException {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("uuid",alarmDto.getUuid()); //事件唯一标识
        map.put("uat",unalarmType.getKey()); //解除警告原因
        map.put("uadt",LocalDateTime.now()); //解除警告时间
        map.put("unalarm",true);
        rocketMQTemplate.syncSend(TopicConstants.EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(map)).build());
    }


}
