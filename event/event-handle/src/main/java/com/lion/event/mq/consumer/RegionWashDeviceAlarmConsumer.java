package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.RegionWashAlarmDto;
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
import java.time.format.DateTimeFormatter;
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
public class RegionWashDeviceAlarmConsumer implements RocketMQListener<MessageExt> {

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
            RegionWashAlarmDto regionWashAlarmDto = jacksonObjectMapper.readValue(msg, RegionWashAlarmDto.class);
            User user = redisUtil.getUserById(regionWashAlarmDto.getUserId());
            if (Objects.nonNull(user) && Objects.equals(regionWashAlarmDto.getAlarmType(), AlarmType.REGION_WASH_ALARM)){
                washAlarm(user, regionWashAlarmDto);
            }

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void washAlarm(User user, RegionWashAlarmDto regionWashAlarmDto) throws JsonProcessingException {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        if (Objects.isNull(userCurrentRegionDto)){
            return;
        }
        if (!Objects.equals(userCurrentRegionDto.getRegionId(), regionWashAlarmDto.getRegionId())){
            //如果用户从需要警告的区域离开则解除警告
//            unalarm(alarmDto,UnalarmType.LEAVE_REGION);
            log.info(user.getName()+"->离开之前的区域,解除警告");
            return;
        }
        Alarm alarm = redisUtil.getAlarm(AlarmClassify.STAFF);
        if (Objects.isNull(alarm)){
            unalarm(regionWashAlarmDto,UnalarmType.NO_WASH_RULE);
            return;
        }
        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
        if (Objects.nonNull(userLastWashDto) && Objects.nonNull(userLastWashDto.getDateTime()) && userLastWashDto.getDateTime().isAfter(regionWashAlarmDto.getAlarmDateTime())){
            List<Wash> washList = redisUtil.getWash(regionWashAlarmDto.getRegionId());
            for (Wash wash :washList){
                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(), wash);
                if (Objects.equals(false, b)) {
                    log.info("->发送洗手警告（未在规定洗手设备洗手）");
                    try {
                        again(regionWashAlarmDto,alarm);
                        storageAlarm(regionWashAlarmDto,alarm);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            };

            //解除警告
            log.info(user.getName()+"->解除警告");
            unalarm(regionWashAlarmDto,UnalarmType.WASH);
            return;
        }
        log.info(user.getName()+"->发送洗手警告（未在规定时间内洗手）");
//        again(alarmDto,alarm);
        storageAlarm(regionWashAlarmDto,alarm);
    }

    private void again(RegionWashAlarmDto regionWashAlarmDto, Alarm alarm) throws JsonProcessingException {
        if (Objects.equals(true,alarm.getAgain())){
            regionWashAlarmDto.setDelayDateTime(LocalDateTime.now().plusMinutes(alarm.getInterval()));
        }
        Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashAlarmDto.getDelayDateTime());
        if (delayLevel > -1) {
//            log.info("推送延迟警告命令");
            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashAlarmDto)).build(), 1000, delayLevel);
        }
    }

    /**
     * 保存警告数据
     * @param regionWashAlarmDto
     * @param alarm
     */
    private void storageAlarm(RegionWashAlarmDto regionWashAlarmDto, Alarm alarm) throws JsonProcessingException {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("typ", Type.STAFF); //警告类型
        map.put("pi", regionWashAlarmDto.getUserId()); //员工id
        map.put("ai", alarm.getId()); //警告id
        map.put("an", alarm.getContent()); //警告名称
        map.put("sdt",LocalDateTime.now()); //警告时间
        map.put("uuid", regionWashAlarmDto.getUuid()); //事件唯一标识
        rocketMQTemplate.syncSend(TopicConstants.ALARM_TO_STORAGE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(map)).build());
    }

    /**
     * 解除警告
     * @param regionWashAlarmDto
     * @param unalarmType
     */
    private void unalarm(RegionWashAlarmDto regionWashAlarmDto, UnalarmType unalarmType) throws JsonProcessingException {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("uuid", regionWashAlarmDto.getUuid()); //事件唯一标识
        map.put("uat",unalarmType.getKey()); //解除警告原因
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        map.put("uadt",df.format(LocalDateTime.now())); //解除警告时间
        map.put("unalarm",true);
        rocketMQTemplate.syncSend(TopicConstants.EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(map)).build());
    }


}
