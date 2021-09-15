package com.lion.event.mq.consumer;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lion.common.constants.RedisConstants;
//import com.lion.common.constants.TopicConstants;
//import com.lion.common.dto.LoopWashDeviceAlarmDto;
//import com.lion.common.dto.LoopWashDto;
//import com.lion.common.dto.UserLastWashDto;
//import com.lion.common.utils.MessageDelayUtil;
//import com.lion.common.utils.RedisUtil;
//import com.lion.event.utils.WashRuleUtil;
//import com.lion.manage.entity.enums.SystemAlarmType;
//import com.lion.manage.entity.enums.WashRuleType;
//import com.lion.manage.entity.rule.Wash;
//import lombok.extern.java.Log;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @Author Mr.Liu
// * @Description
// * @Date 2021/5/19 下午3:44
// **/
//@Component
//@RocketMQMessageListener(topic = TopicConstants.LOOP_WASH_DEVICE_ALARM ,selectorExpression="*",consumerGroup = TopicConstants.LOOP_WASH_DEVICE_ALARM_CONSUMER_GROUP)
//@Log
//public class LoopWashDeviceAlarmConsumer implements RocketMQListener<MessageExt> {
//
//    @Autowired
//    private ObjectMapper jacksonObjectMapper;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Autowired
//    private RocketMQTemplate rocketMQTemplate;
//
//    @Autowired
//    private WashRuleUtil washRuleUtil;
//
//    @Override
//    public void onMessage(MessageExt messageExt) {
//        try {
//            byte[] body = messageExt.getBody();
//            String msg = new String(body);
//            LoopWashDeviceAlarmDto loopWashDeviceAlarmDto = jacksonObjectMapper.readValue(msg, LoopWashDeviceAlarmDto.class);
//            LocalDateTime dateTime = LocalDateTime.now();
//            if (!(loopWashDeviceAlarmDto.getStartAlarmDateTime().isBefore(dateTime) && dateTime.isBefore(loopWashDeviceAlarmDto.getEndAlarmDateTime()))) {
//                return;
//            }
//            String state = (String) redisTemplate.opsForValue().get(RedisConstants.USER_WORK_STATE+loopWashDeviceAlarmDto.getUserId());
//            String uuid = (String) redisTemplate.opsForValue().get(RedisConstants.USER_WORK_STATE_UUID+loopWashDeviceAlarmDto.getUserId());
//            if (Objects.isNull(state) || Objects.equals(RedisConstants.USER_WORK_STATE_END,state) || !Objects.equals(uuid,loopWashDeviceAlarmDto.getUuid())) {
//                return;
//            }
//
////            List<Wash> list = redisUtil.getLoopWash();
////            if (Objects.isNull(list) || list.size()<=0) {
////                if (Objects.nonNull(loopWashDeviceAlarmDto.getUserId())) {
////                    list = redisUtil.getLoopWashByUserId(loopWashDeviceAlarmDto.getUserId());
////                }
////            }
////            if (Objects.nonNull(list) && list.size()<=0) {
////                return;
////            }
////            list.forEach(wash -> {
////                if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.equals(wash.getRemind(),true)) {
////                    Duration duration = Duration.between(LocalDateTime.now(), loopWashDeviceAlarmDto.getDeviceDelayAlarmDateTime());
////                    long millis = duration.toMillis();
////                    if (millis<1000){
////                        UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+loopWashDeviceAlarmDto.getUserId());
////                        if (Objects.isNull(userLastWashDto)) {
////                            log.info("给硬件发送提醒("+loopWashDeviceAlarmDto.getCount()+"次)");
////                        }else {
////                            Boolean b = washRuleUtil.judgeDevideType(userLastWashDto.getMonitorId(),wash);
////                            if (!b){
////                                log.info("给硬件发送提醒("+loopWashDeviceAlarmDto.getCount()+"次)");
////                            }else {
////                                return;
////                            }
////                        }
////                        loopWashDeviceAlarmDto.setDeviceDelayAlarmDateTime(LocalDateTime.now().plusMinutes(wash.getOvertimeRemind()));
////                        loopWashDeviceAlarmDto.setCount(loopWashDeviceAlarmDto.getCount()+1);
////                        if (loopWashDeviceAlarmDto.getDeviceDelayAlarmDateTime().isAfter(loopWashDeviceAlarmDto.getEndAlarmDateTime())) {
////                            return;
////                        }
////                    }
////                    //delay(loopWashDeviceAlarmDto);
////                }
////            });
//        }catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    private void delay(LoopWashDeviceAlarmDto loopWashDeviceAlarmDto){
//        Integer delayLevel = MessageDelayUtil.getDelayLevel(loopWashDeviceAlarmDto.getDeviceDelayAlarmDateTime());
//        if (delayLevel > -1) {
//            try {
//                rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH_DEVICE_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDeviceAlarmDto)).build(), 1000, delayLevel);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
