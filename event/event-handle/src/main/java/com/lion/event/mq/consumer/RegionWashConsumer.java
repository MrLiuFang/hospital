package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.AlarmDto;
import com.lion.common.dto.RegionWashDto;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.EventAlarmType;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.common.enums.AlarmType;
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
import java.util.*;

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
            String uuid = UUID.randomUUID().toString();
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
                        alarmDto.setAlarmType(AlarmType.REGION_WASH_ALARM);
                        alarmDto.setUserId(userCurrentRegionDto.getUserId());
                        alarmDto.setUuid(uuid);
                        for (Wash wash :washList){
                            Map<String,Object> event = new HashMap<String,Object>();
                            event.put("typ", Type.STAFF.getKey());//类型
                            event.put("ia",false);//是否触发警告
                            event.put("sdt",LocalDateTime.now());//系统时间
                            //进入X区域之前X分钟洗手检测
                            if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime()) && wash.getBeforeEnteringTime() >0){
                                //没有最后的洗手记录
                                if (Objects.isNull(userLastWashDto)) {
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRQRXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                  log.info("推送延迟警告命令");
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    return;
                                }
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRHWZGDDSBXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                    log.info("推送告警没有按洗手规则中规定的设备来洗手");
                                    return;
                                }
                                //超过时间范围
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                                if (duration.toMinutes() > wash.getBeforeEnteringTime()){
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRQRXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                  log.info("推送延迟警告命令");
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    return;
                                }
                            }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getAfterEnteringTime()) && wash.getAfterEnteringTime() >0){
                                //进入X区域之后X分钟洗手检测
                                //没有最后的洗手记录
                                if (Objects.isNull(userLastWashDto)) {
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRHWXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                  log.info("推送延迟警告命令");
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    return;
                                }
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRHWZGDDSBXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                    log.info("推送告警没有按洗手规则中规定的设备来洗手");
                                    return;
                                }
                                //超过时间范围
                                if(Objects.nonNull(userCurrentRegionDto.getFirstEntryTime()) &&
                                        !userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime().plusMinutes(wash.getAfterEnteringTime())) &&
                                        !userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime())){
                                    event.put("ia",true);//是否触发警告
                                    event.put("at", EventAlarmType.JRHWXS.getKey());//警告触发原因
                                    producerEvent(event,userCurrentRegionDto,uuid);
//                                  log.info("推送延迟警告命令");
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_ALARM_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(alarmDto)).build());
                                    return;
                                }
                            }
                            producerEvent(event,userCurrentRegionDto,uuid);



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
                        }
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void producerEvent(Map<String,Object> event,UserCurrentRegionDto userCurrentRegionDto,String uuid){
        event.put("pi",userCurrentRegionDto.getUserId());//员工id
        event.put("ri",userCurrentRegionDto.getRegionId());//区域id
        event.put("uuid",uuid);//事件uuid
        try {
            rocketMQTemplate.syncSend(TopicConstants.EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(event)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
