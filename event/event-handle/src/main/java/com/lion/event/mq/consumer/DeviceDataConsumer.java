package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.enums.TagType;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.service.UserWashService;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 **/

@Component
@RocketMQMessageListener(topic = TopicConstants.DEVICE_DATA,selectorExpression="*",consumerGroup = TopicConstants.DEVICE_DATA_CONSUMER_GROUP)
@Log
public class DeviceDataConsumer implements RocketMQListener<MessageExt> {


    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserWashService userWashService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            DeviceDataDto deviceDataDto = jacksonObjectMapper.readValue(msg, DeviceDataDto.class);
            deviceDataDto.setSystemDateTime(LocalDateTime.now());
            Device monitor = null;
            Device star = null;
            Tag tag = null;
            User user = null;
            if (Objects.nonNull(deviceDataDto.getMonitorId())) {
                monitor = redisUtil.getDevice(deviceDataDto.getMonitorId());
            }
            if (Objects.nonNull(deviceDataDto.getStarId())) {
                star = redisUtil.getDevice(deviceDataDto.getStarId());
            }
            if (Objects.nonNull(deviceDataDto.getTagId())) {
                tag = redisUtil.getTag(deviceDataDto.getTagId());
            }
            if (Objects.nonNull(tag)){
                user = redisUtil.getUser(tag.getId());
            }

            if (Objects.nonNull(user)){ //如果根据标签查出用户，进行洗手事件处理
                userWashService.userWashEevent(deviceDataDto,monitor,star,tag,user);
            }
//            else  if (Objects.isNull()) { //处理患者数据
//
//            }else  if (Objects.isNull()) { //处理流动人员数据
//
//            }

            else if (Objects.nonNull(tag) && (Objects.equals(deviceDataDto.getTagType(), TagType.ASSET) || Objects.equals(deviceDataDto.getTagType(), TagType.HUMIDITY) || Objects.equals(deviceDataDto.getTagType(), TagType.TEMPERATUE) ) && (Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH) || Objects.equals(tag.getPurpose(), TagPurpose.ASSETS) )){ //处理设备(资产,温湿仪等)数据

            }


        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


}
