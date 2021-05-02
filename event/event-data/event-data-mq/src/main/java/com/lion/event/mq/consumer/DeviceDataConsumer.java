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
import com.lion.event.entity.DeviceData;
import com.lion.event.entity.enums.Type;
import com.lion.event.service.DeviceDataService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.region.Region;
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
@RocketMQMessageListener(topic = TopicConstants.EVENT,selectorExpression="*",consumerGroup = TopicConstants.EVENT_TO_STORAGE_GROUP)
@Log
public class DeviceDataConsumer implements RocketMQListener<MessageExt> {


    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private RedisUtil redisUtil;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

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
            Region monitorRegion = null;
            Region starRegion = null;
            Region region = null;
            DeviceData deviceData = new DeviceData();
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
            if (Objects.nonNull(monitor) && Objects.nonNull(monitor.getId())) {
                monitorRegion = redisUtil.getRegion(monitor.getId());
            }
            if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
                starRegion = redisUtil.getRegion(star.getId());
            }
            region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
            if (Objects.nonNull(user)){
                deviceData.setTyp(Type.STAFF.getKey());
                deviceData.setPi(user.getId());
            }

            if (Objects.nonNull(monitor)) {
                deviceData.setMc(deviceDataDto.getMonitorId());
                deviceData.setMb(deviceDataDto.getMonitorBattery());
                deviceData.setMcl(monitor.getDeviceClassify().getKey());
                deviceData.setMn(monitor.getName());
                deviceData.setMt(monitor.getDeviceType().getKey());
            }
            if (Objects.nonNull(star)){
                deviceData.setSc(deviceDataDto.getStarId());
                deviceData.setScl(star.getDeviceClassify().getKey());
                deviceData.setSn(star.getName());
                deviceData.setSt(star.getDeviceType().getKey());
            }
            if (Objects.nonNull(tag)){
                deviceData.setBi(deviceDataDto.getButtonId());
                deviceData.setTb(deviceDataDto.getTagBattery());
                deviceData.setTc(deviceDataDto.getTagId());
                deviceData.setTyp(tag.getType().getKey());
                deviceData.setTp(tag.getPurpose().getKey());
                deviceData.setTn(tag.getDeviceName());
            }
            if (Objects.nonNull(region)){
                deviceData.setRi(region.getId());
                deviceData.setRn(region.getName());
                Build build = redisUtil.get
            }

            deviceData.setW(deviceDataDto.getWarning());
            deviceData.setT(deviceDataDto.getTemperature());
            deviceData.setH(deviceDataDto.getHumidity());
            deviceData.setDdt(deviceDataDto.getTime());
            deviceDataService.save(deviceData);

            // 更新设备的电量
            updateDeviceBattery(monitor,deviceDataDto.getMonitorBattery());
            updateTagBattery(tag,deviceDataDto.getTagBattery());

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }



    private void updateDeviceBattery(Device device,Integer battery){
        if (Objects.nonNull(device)){
            if (!Objects.equals(device.getBattery(),battery)){
                deviceExposeService.updateBattery(device.getId(),battery);
            }
        }
    }

    private void updateTagBattery(Tag tag,Integer battery){
        if (Objects.nonNull(tag)){
            if (!Objects.equals(tag.getBattery(),battery)){
                tagExposeService.updateBattery(tag.getId(),battery);
            }
        }
    }

}
