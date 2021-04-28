package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.RedisConstants;
import com.lion.event.constant.TopicConstants;
import com.lion.event.dto.AlarmDto;
import com.lion.event.dto.RegionWashDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.entity.enums.AlarmType;
import com.lion.event.utils.RedisUtil;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
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
 * @Description //洗手监控
 * @Date 2021/4/25 下午3:53
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.REGION_WASH ,selectorExpression="*",consumerGroup = TopicConstants.REGION_WASH_CONSUMER_GROUP)
@Log
public class RegionWashConsumer implements RocketMQListener<MessageExt> {

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

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            RegionWashDto regionWashDto = jacksonObjectMapper.readValue(msg, RegionWashDto.class);
            if (Objects.nonNull(regionWashDto) && Objects.nonNull(regionWashDto.getUserId())) {
                UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + regionWashDto.getUserId());
                if (Objects.nonNull(userCurrentRegionDto)) {
                    //判断用户是否从X区域离开 如果离开就不进行洗手检测
                    if (Objects.nonNull(userCurrentRegionDto.getRegionId()) && Objects.equals(userCurrentRegionDto.getRegionId(),regionWashDto.getRegionId())) {
                        List<Wash> washList = redisUtil.getWash(regionWashDto.getRegionId());
                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+regionWashDto.getUserId());
                        AlarmDto alarmDto = new AlarmDto();
                        alarmDto.setAlarmDateTime(LocalDateTime.now());
                        alarmDto.setRegionId(userCurrentRegionDto.getRegionId());
                        alarmDto.setAlarmType(AlarmType.region_wash_alarm);
                        alarmDto.setUserId(userCurrentRegionDto.getUserId());
                        washList.forEach(wash -> {
                            //进入X区域之前X分钟洗手检测
                            if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime()) && wash.getBeforeEnteringTime() >0){
                                //没有最后的洗手记录
                                if (Objects.isNull(userLastWashDto)) {
                                    try {
//                                        log.info("推送延迟警告命令");
                                        rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
//                                Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(),wash);
//                                if (Objects.equals(false,b)){
//                                    // TODO: 2021/4/25  推送告警
//                                    log.info("推送告警没有按规则洗手设备类型来洗手");
//                                    return;
//                                }
                                //超过时间范围
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                                if (duration.toMinutes() > wash.getBeforeEnteringTime()){
                                    try {
                                        log.info("推送延迟警告命令");
                                        rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                            }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getAfterEnteringTime()) && wash.getAfterEnteringTime() >0){
                                //进入X区域之后X分钟洗手检测
                                //没有最后的洗手记录
                                if (Objects.isNull(userLastWashDto)) {
                                    try {
//                                        log.info("推送延迟警告命令");
                                        rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
//                                Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(),wash);
//                                if (Objects.equals(false,b)){
//                                    // TODO: 2021/4/25  推送告警
//                                    log.info("推送告警没有按规则洗手设备类型来洗手");
//                                    return;
//                                }
                                //超过时间范围
                                if(Objects.nonNull(userCurrentRegionDto.getFirstEntryTime()) &&
                                        !userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime().plusMinutes(wash.getAfterEnteringTime())) &&
                                        !userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime())){
                                    try {
//                                        log.info("推送延迟警告命令");
                                        rocketMQTemplate.syncSend(TopicConstants.ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                            }

    //                        else if (Objects.equals(wash.getType(), WashRuleType.LOOP)){
    //                            UserLastWashDto previous = userLastWashDto.getPrevious();
    //                            if (wash.getDuration()>userLastWashDto.getTime()){
    //                                // TODO: 2021/4/25 推送洗手时间不够告警
    //                                log.info("推送洗手时间不够告警");
    //                            }
    //                            Duration duration = Duration.between( userLastWashDto.getDateTime(),LocalDateTime.now());
    //                            if (duration.toMinutes()>wash.getInterval()){
    //                                // TODO: 2021/4/25 推送洗手间隔警告
    //                                log.info("推送洗手间隔警告:1");
    //                            }else {
    //                                if (Objects.nonNull(previous)) {
    //                                    Duration duration1 = Duration.between(previous.getDateTime(),userLastWashDto.getDateTime());
    //                                    if (duration1.toMinutes() > wash.getInterval()) {
    //                                        // TODO: 2021/4/25 推送洗手间隔警告
    //                                        log.info("推送洗手间隔警告:2");
    //                                    }
    //                                }
    //                            }
    //
    //                            if (userLastWashDto.getDateTime().plusMinutes(wash.getInterval()).isAfter(LocalDateTime.now())){
    //                                try {
    //                                    RegionWashDelayDto regionWashDelayDto = new RegionWashDelayDto();
    //                                    regionWashDelayDto.setUserId(userCurrentRegionDto.getUserId());
    //                                    regionWashDelayDto.setRegionId(userCurrentRegionDto.getRegionId());
    //                                    regionWashDelayDto.setDelayDateTime(userLastWashDto.getDateTime().plusMinutes(wash.getInterval()));
    //                                    Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashDelayDto.getDelayDateTime());
    //                                    if (delayLevel > -1) {
    //                                        rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build(), 1000, delayLevel);
    //                                    }
    //                                } catch (JsonProcessingException e) {
    //                                    e.printStackTrace();
    //                                }
    //                            }
    //                        }
                        });
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


}
