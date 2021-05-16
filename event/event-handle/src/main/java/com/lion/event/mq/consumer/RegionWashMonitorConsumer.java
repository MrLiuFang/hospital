package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.RegionWashAlarmDto;
import com.lion.common.dto.RegionWashMonitorDelayDto;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.WashEventAlarmType;
import com.lion.common.enums.Type;
import com.lion.common.enums.WashEventType;
import com.lion.common.utils.RedisUtil;
import com.lion.common.enums.AlarmType;
import com.lion.device.entity.tag.Tag;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.user.User;
import com.lion.utils.CurrentUserUtil;
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
                        RegionWashAlarmDto regionWashAlarmDto = new RegionWashAlarmDto();
                        regionWashAlarmDto.setAlarmDateTime(LocalDateTime.now());
                        regionWashAlarmDto.setRegionId(userCurrentRegionDto.getRegionId());
                        regionWashAlarmDto.setAlarmType(AlarmType.REGION_WASH_ALARM);
                        regionWashAlarmDto.setUserId(userCurrentRegionDto.getUserId());
                        regionWashAlarmDto.setUuid(userCurrentRegionDto.getUuid());
                        for (Wash wash :washList){
                            Map<String,Object> event = new HashMap<String,Object>();
                            event.put("typ", Type.STAFF.getKey());//类型
                            event.put("ia",false);//是否触发警告
                            event.put("wet", WashEventType.REGION.getKey());
                            event.put("sdt",LocalDateTime.now());//系统时间
                            //进入X区域之前X分钟洗手检测
                            if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime()) && wash.getBeforeEnteringTime() >0){
                                //没有最后的洗手记录(未洗手)
                                if (Objects.isNull(userLastWashDto)) {
                                    alarm(event,true,WashEventAlarmType.JRQRXS,null,userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                                //超过时间范围(未洗手)
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                                if (duration.toMinutes() > wash.getBeforeEnteringTime()){
                                    alarm(event,true,WashEventAlarmType.JRQRXS,null,userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                                //判断是否用规定的洗手设备洗手
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    alarm(event,true,WashEventAlarmType.JRHWZGDDSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                            }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getAfterEnteringTime()) && wash.getAfterEnteringTime() >0){
                                //进入X区域之后X分钟洗手检测
                                //没有最后的洗手记录(未洗手)
                                if (Objects.isNull(userLastWashDto)) {
                                    alarm(event,true,WashEventAlarmType.JRHWXS,null,userCurrentRegionDto,null,wash);
                                    return;
                                }
                                //超过时间范围(未洗手)
                                if(Objects.nonNull(userCurrentRegionDto.getFirstEntryTime()) &&
                                        !userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime().plusMinutes(wash.getAfterEnteringTime())) &&
                                        !userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime())){
                                    alarm(event,true,WashEventAlarmType.JRHWXS,null,userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                                //判断有没有在规定的洗手设备洗手
                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
                                if (Objects.equals(false,b)){
                                    alarm(event,true,WashEventAlarmType.JRHWZGDDSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash);
                                    return;
                                }
                            }
                            recordWashEvent(event,userCurrentRegionDto,userCurrentRegionDto.getUuid(),userLastWashDto.getMonitorId());



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

    private void alarm(Map<String,Object> event,Boolean ia,WashEventAlarmType washEventAlarmType,LocalDateTime wt,UserCurrentRegionDto userCurrentRegionDto,UserLastWashDto userLastWashDto,Wash wash){
        if (Objects.nonNull(ia)) {
            event.put("ia", true);//是否触发警告
        }
        if (Objects.nonNull(washEventAlarmType)) {
            event.put("at", washEventAlarmType.getKey());//警告触发原因
        }
        if (Objects.nonNull(wt)) {
            event.put("wt", wt); //洗手时间
        }
        recordWashEvent(event,userCurrentRegionDto,userCurrentRegionDto.getUuid(),Objects.isNull(userLastWashDto)?null:userLastWashDto.getMonitorId());
        sendAlarmToTag(userCurrentRegionDto.getUserId(),wash);
    }

    /**
     * 给员工发送警告(设备 tag)
     * @param userId
     * @param wash
     */
    private void sendAlarmToTag(Long userId, Wash wash){
        if (Objects.equals(true,wash.getRemind())) {
            User user = redisUtil.getUserById(userId);
            if (Objects.nonNull(user)) {
                log.info("给"+user.getTagCode()+"发送设备警报");
            }
            //系统内警告

        }
    }

    /**
     * 记录洗手事件
     * @param event
     * @param userCurrentRegionDto
     * @param uuid
     * @param monitorId
     */
    private void recordWashEvent(Map<String,Object> event, UserCurrentRegionDto userCurrentRegionDto, String uuid, Long monitorId){
        event.put("pi",userCurrentRegionDto.getUserId());//员工id
        event.put("ri",userCurrentRegionDto.getRegionId());//区域id
        event.put("dvi",monitorId);//洗手设备id
        event.put("uuid",uuid);//事件uuid
        try {
            rocketMQTemplate.syncSend(TopicConstants.WASH_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(event)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
