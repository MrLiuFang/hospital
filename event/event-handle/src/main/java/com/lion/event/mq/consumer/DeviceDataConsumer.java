package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.utils.RedisUtil;
import com.lion.common.constants.TopicConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dto.RegionWashDelayDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.service.UserWashService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
//            }else { //处理资产设备数据
//
//            }


        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


}
