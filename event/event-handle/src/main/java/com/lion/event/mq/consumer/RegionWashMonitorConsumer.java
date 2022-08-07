package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.enums.Type;
import com.lion.common.enums.WashEventType;
import com.lion.common.utils.RedisUtil;
import com.lion.event.mq.consumer.common.WashCommon;
import com.lion.event.utils.WashRuleUtil;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.vo.ListWashTemplateItemVo;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
                    String str = (String) redisTemplate.opsForValue().get(RedisConstants.WASH_MONITOR +regionWashMonitorDelayDto.getUserId());
                    if (Objects.isNull(str)) {
                        return;
                    }
                    String regionId = str.split("_")[0];
                    String uuid = str.split("_")[1];
                    if (!(Objects.equals(regionId,String.valueOf(regionWashMonitorDelayDto.getRegionId())) && Objects.equals(uuid,regionWashMonitorDelayDto.getMonitorId()))) {
                        return;
                    }
                    //判断用户是否从X区域离开 如果离开就不进行洗手检测
                    if (Objects.nonNull(userCurrentRegionDto.getRegionId()) && Objects.equals(userCurrentRegionDto.getRegionId(),regionWashMonitorDelayDto.getRegionId())) {
                        Region region = redisUtil.getRegionById(userCurrentRegionDto.getRegionId());
                        ListWashTemplateItemVo washTemplateItemVo = redisUtil.getWashTemplate(region.getWashTemplateId());
                        if (Objects.isNull(washTemplateItemVo)) {
                            return;
                        }
                        WashRecordDto washRecordDto = washCommon.init(userCurrentRegionDto.getUserId(),userCurrentRegionDto.getRegionId(),null,userCurrentRegionDto.getUuid() , null,null);
                        WashEventDto washEventDto = new WashEventDto();
                        BeanUtils.copyProperties(washRecordDto,washEventDto);
                        washEventDto.setWi(washTemplateItemVo.getId());
                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+regionWashMonitorDelayDto.getUserId());
                        if ( Objects.nonNull(washTemplateItemVo.getBeforeTime()) && washTemplateItemVo.getBeforeTime() >0){
                            if (Objects.nonNull(washTemplateItemVo.getNoCheckTime())) {
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                                if (duration.getSeconds() <= washTemplateItemVo.getNoCheckTime() ) {
                                    return;
                                }
                            }
                            String before = (String) redisTemplate.opsForValue().get(RedisConstants.BEFORE_UUID+uuid);
                            redisTemplate.opsForValue().set(RedisConstants.BEFORE_UUID+uuid,uuid,24, TimeUnit.DAYS);
                            if (Objects.isNull(before)) {
                                if (Objects.isNull(userLastWashDto)) {
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                    return;
                                }
                                Duration duration = Duration.between(userLastWashDto.getDateTime(),userCurrentRegionDto.getFirstEntryTime());
                                if (duration.getSeconds() > washTemplateItemVo.getBeforeTime() && userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime())){
                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                    return;
                                }
                                Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(), washTemplateItemVo.getWashDeviceTypes());
                                if (Objects.equals(b,false)){
                                    alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                    return;
                                }
                            }
                        }
                        if (Objects.nonNull(washTemplateItemVo.getAfterTime()) && washTemplateItemVo.getAfterTime() >0){
                            if (Objects.isNull(userLastWashDto)) {
                                alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                return;
                            }
                            if (userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime())) {
                                alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                return;
                            }
                            if((userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime().plusSeconds(washTemplateItemVo.getAfterTime())) && userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime()))){
                                alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                return;
                            }

                            Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(), washTemplateItemVo.getWashDeviceTypes());
                            if (Objects.equals(b,false)){
                                alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,washTemplateItemVo,regionWashMonitorDelayDto.getTagId() );
                                return;
                            }
                        }
                        recordWashEvent(washEventDto);
//                        List<Wash> washList = redisUtil.getWash(regionWashMonitorDelayDto.getRegionId());
//                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+regionWashMonitorDelayDto.getUserId());
//                        for (Wash wash :washList){
//                            WashRecordDto washRecordDto = washCommon.init(userCurrentRegionDto.getUserId(),userCurrentRegionDto.getRegionId(),null,userCurrentRegionDto.getUuid() ,
//                                    null,null);
//                            BeanUtils.copyProperties(washRecordDto,washEventDto);
//                            washEventDto.setWi(wash.getId());
//                            //进入X区域之前X分钟洗手检测
//                            if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getBeforeEnteringTime()) && wash.getBeforeEnteringTime() >0){
//                                //没有最后的洗手记录(未洗手)
//                                if (Objects.isNull(userLastWashDto)) {
//                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }
//                                //超过时间范围(未洗手)
//                                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
//                                if (duration.toMinutes() > wash.getBeforeEnteringTime()){
//                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }else {
//                                    //洗手时长不够
//                                    if (userLastWashDto.getTime()<wash.getDuration()) {
//                                        alarm(washEventDto,true,SystemAlarmType.WDDBZSXSC,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash,regionWashMonitorDelayDto.getTagId() );
//                                        return;
//                                    }
//                                }
//
//                                //判断是否用规定的洗手设备洗手
//                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
//                                if (Objects.equals(b,false)){
//                                    alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }
//                            }else if (Objects.equals(wash.getType(), WashRuleType.REGION) && Objects.nonNull(wash.getAfterEnteringTime()) && wash.getAfterEnteringTime() >0){
//                                //进入X区域之后X分钟洗手检测
//                                //没有最后的洗手记录(未洗手)
//                                if (Objects.isNull(userLastWashDto)) {
//                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,null,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }
//                                //超过时间范围(未洗手)
//                                if(!(userLastWashDto.getDateTime().isBefore(userCurrentRegionDto.getFirstEntryTime().plusMinutes(wash.getAfterEnteringTime())) && userLastWashDto.getDateTime().isAfter(userCurrentRegionDto.getFirstEntryTime()))){
//                                    alarm(washEventDto,true,SystemAlarmType.ZZDQYWJXXSCZ,null,userCurrentRegionDto,userLastWashDto,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }
//                                //判断有没有在规定的洗手设备洗手
//                                Boolean b = washRuleUtil.judgeDevide(userLastWashDto.getMonitorId(),wash);
//                                if (Objects.equals(b,false)){
//                                    alarm(washEventDto,true,SystemAlarmType.WXYBZDXSSBXS,userLastWashDto.getDateTime(),userCurrentRegionDto,userLastWashDto,wash,regionWashMonitorDelayDto.getTagId() );
//                                    return;
//                                }
//                            }
//                            recordWashEvent(washEventDto);
//                        }
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void alarm(WashEventDto washEventDto,Boolean ia,SystemAlarmType systemAlarmType,LocalDateTime wt,UserCurrentRegionDto userCurrentRegionDto,UserLastWashDto userLastWashDto,WashTemplateItem wash,Long tagId) throws JsonProcessingException {
        washEventDto.setWet(WashEventType.REGION.getKey());
        washEventDto.setIa(ia);
        if (Objects.nonNull(systemAlarmType)) {
            washEventDto.setAt(systemAlarmType.getKey());
        }
        if (Objects.nonNull(wt)) {
            washEventDto.setWt(wt);
        }
        recordWashEvent(washEventDto);
        sendAlarmToTag(userCurrentRegionDto.getUserId(),wash,userCurrentRegionDto.getUuid(),systemAlarmType, userCurrentRegionDto,tagId );

        if (Objects.equals(wash.getIsAlarm(),true)) {
            //系统内警告
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(Type.STAFF);
            systemAlarmDto.setTagId(tagId);
            systemAlarmDto.setRegionId(Objects.nonNull(userCurrentRegionDto) ? userCurrentRegionDto.getRegionId() : null);
            systemAlarmDto.setPeopleId(userCurrentRegionDto.getUserId());
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setSystemAlarmType(systemAlarmType);
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        }
    }

    /**
     * 给员工发送警告(设备 tag)
     * @param userId
     * @param wash
     * @param userCurrentRegionDto
     */
    private void sendAlarmToTag(Long userId, WashTemplateItem wash, String uuid, SystemAlarmType systemAlarmType, UserCurrentRegionDto userCurrentRegionDto, Long tagId) throws JsonProcessingException {
        if (Objects.equals(wash.getIsRemind(),true)) {
            User user = redisUtil.getUserById(userId);
            if (Objects.nonNull(user)) {
                // TODO: 2021/5/17 给设备发送数据
                log.info("给"+user.getTagCode()+"发送设备警报");
            }
        }

        if (Objects.equals(wash.getIsBell(),true)) {
            // TODO: 2021/9/14 发送警示铃
            log.info("发送警示铃");
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


