package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.service.DeviceService;
import com.lion.event.service.PatientService;
import com.lion.event.service.UserWashService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
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

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private PatientService patientService;

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
            Patient patient = null;
            TemporaryPerson temporaryPerson = null;
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
                if (Objects.isNull(user)) {
                    patient = redisUtil.getPatientByTagId(tag.getId());
                }else if (Objects.isNull(patient)) {
                    temporaryPerson = redisUtil.getTemporaryPersonByTagId(tag.getId());
                }
            }

            if (Objects.nonNull(user) && Objects.equals(deviceDataDto.getTagType(), Type.STAFF) ){ //如果根据标签查出员工，进行洗手事件处理
                userWashService.userWashEevent(deviceDataDto,monitor,star,tag,user);
            }else if (Objects.nonNull(patient)  ) { //处理患者数据
                patientService.patientEvent(deviceDataDto,monitor,star,tag,patient);
            }else if (Objects.nonNull(temporaryPerson)) { //处理流动人员数据

            }else if (Objects.nonNull(tag)
                    && (Objects.equals(deviceDataDto.getTagType(), Type.ASSET) || Objects.equals(deviceDataDto.getTagType(), Type.DEVICE) || Objects.equals(deviceDataDto.getTagType(), Type.HUMIDITY) || Objects.equals(deviceDataDto.getTagType(), Type.TEMPERATURE) )
                    && (Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH) || Objects.equals(tag.getPurpose(), TagPurpose.ASSETS) )){ //处理设备(资产,温湿仪等)数据
                deviceService.deviceEevent(deviceDataDto,monitor,star,tag);
            }


        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


}
