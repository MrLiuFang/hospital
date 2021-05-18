package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.Type;
import com.lion.common.enums.WashEventType;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.event.mq.consumer.common.WashCommon;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.user.User;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //洗手监控
 * @Date 2021/4/25 下午3:53
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.REGION_WASH ,selectorExpression="*",consumerGroup = TopicConstants.REGION_WASH_CONSUMER_GROUP)
@Log
public class RegionWashMonitorConsumer implements RocketMQListener<MessageExt> {

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
            RegionWashMonitorDelayDto regionWashMonitorDelayDto = jacksonObjectMapper.readValue(msg, RegionWashMonitorDelayDto.class);
            if (Objects.nonNull(regionWashMonitorDelayDto) && Objects.nonNull(regionWashMonitorDelayDto.getUserId())) {
                UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + regionWashMonitorDelayDto.getUserId());
                if (Objects.nonNull(userCurrentRegionDto)) {
                    //判断用户是否从X区域离开 如果离开就不进行洗手检测
                    if (Objects.nonNull(userCurrentRegionDto.getRegionId()) && Objects.equals(userCurrentRegionDto.getRegionId(),regionWashMonitorDelayDto.getRegionId())) {
                        List<Wash> washList = redisUtil.getWash(regionWashMonitorDelayDto.getRegionId());
                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+regionWashMonitorDelayDto.getUserId());
                        for (Wash wash :washList){
                            WashRecordDto washRecordDto = washCommon.init(userCurrentRegionDto.getUserId(),userCurrentRegionDto.getRegionId(),Objects.isNull(userLastWashDto)?null:userLastWashDto.getMonitorId(),userCurrentRegionDto.getUuid() ,
                                    null,null);
                            WashEventDto washEventDto = new WashEventDto();
                            BeanUtils.copyProperties(washRecordDto,washEventDto);
                            //进入X区域之前X分钟洗手检测
                            if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime()) && wash.getBeforeEnteringTime() >0){
                                //没有最后的洗手记录(未洗手)
                                if (Objects.isNull(userLastWashDto)) {
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,wash);
                                    return;
                                }
                                //超过时间范围(未洗手)
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                                if (duration.toMinutes() > wash.getBeforeEnteringTime()){
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                                //判断是否用规定的洗手设备洗手
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                            }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getAfterEnteringTime()) && wash.getAfterEnteringTime() >0){
                                //进入X区域之后X分钟洗手检测
                                //没有最后的洗手记录(未洗手)
                                if (Objects.isNull(userLastWashDto)) {
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,wash);
                                    return;
                                }
                                //超过时间范围(未洗手)
                                if(Objects.nonNull(userCurrentRegionDto.getFirstEntryTime()) &&
                                        !userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime().plusMinutes(wash.getAfterEnteringTime())) &&
                                        !userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime())){
                                    alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,null,userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                                //判断有没有在规定的洗手设备洗手
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                            }
                            recordWashEvent(washEventDto);



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

    private void alarm(WashEventDto washEventDto,Boolean ia,SystemAlarmType systemAlarmType,LocalDateTime wt,UserCurrentRegionDto userCurrentRegionDto,UserLastWashDto userLastWashDto,Wash wash) throws JsonProcessingException {
        washEventDto.setWet(WashEventType.REGION.getKey());
        if (Objects.nonNull(ia)) {
            washEventDto.setIa(ia);//是否触发警告
        }
        if (Objects.nonNull(systemAlarmType)) {
            washEventDto.setAt(systemAlarmType.getKey());
        }
        if (Objects.nonNull(wt)) {
            washEventDto.setWt(wt);
        }
        recordWashEvent(washEventDto);
        sendAlarmToTag(userCurrentRegionDto.getUserId(),wash,userCurrentRegionDto.getUuid(),systemAlarmType);
    }

    /**
     * 给员工发送警告(设备 tag)
     * @param userId
     * @param wash
     */
    private void sendAlarmToTag(Long userId, Wash wash, String uuid, SystemAlarmType systemAlarmType) throws JsonProcessingException {
        if (Objects.equals(true,wash.getRemind())) {
            User user = redisUtil.getUserById(userId);
            if (Objects.nonNull(user)) {
                // TODO: 2021/5/17 给设备发送数据
                log.info("给"+user.getTagCode()+"发送设备警报");
            }
            //系统内警告
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(Type.STAFF);
            systemAlarmDto.setPeopleId(userId);
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setUuid(uuid);
            systemAlarmDto.setSystemAlarmType(systemAlarmType);
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        }
    }

    /**
     * 记录洗手事件
     */
    private void recordWashEvent(WashEventDto washEventDto){
        try {
            rocketMQTemplate.syncSend(TopicConstants.WASH_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(washEventDto)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
