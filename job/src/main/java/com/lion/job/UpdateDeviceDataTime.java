package com.lion.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午4:04
 */
@Component
public class UpdateDeviceDataTime {

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        {
            List<Long> userId = userExposeService.allId();
            if (Objects.nonNull(userId) && userId.size()>=0) {
                userId.forEach(id -> {
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
                        userExposeService.updateDeviceDataTime(id, dateTime);
                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
                    }
                });
                userId.clear();
                userId = null;
            }
        }
        {
            List<Long> assetsId = assetsExposeService.allId();
            if (Objects.nonNull(assetsId) && assetsId.size()>=0) {
                assetsId.forEach(id -> {
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
                        assetsExposeService.updateDeviceDataTime(id, dateTime);
                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
                    }
                });
                assetsId.clear();
                assetsId = null;
            }
        }
        {
            List<Long> deviceId = deviceExposeService.allId();
            if (Objects.nonNull(deviceId) && deviceId.size()>=0) {
                deviceId.forEach(id -> {
                    Device device = null;
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
                        deviceExposeService.updateDeviceDataTime(id, dateTime);
                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));

                    } else {
                        device = deviceExposeService.findById(id);
                        if (Objects.nonNull(device)) {
                            dateTime = device.getLastDataTime();
                        }
                    }
                    if (Objects.nonNull(dateTime)) {
                        java.time.Duration duration = java.time.Duration.between(dateTime, LocalDateTime.now());
                        if (duration.toMinutes() > (60 * 24)) {
                            if (!Objects.equals(device.getDeviceState(), State.ALARM)) {
                                SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
                                systemAlarmDto.setDateTime(LocalDateTime.now());
                                systemAlarmDto.setType(Type.DEVICE);
                                systemAlarmDto.setDeviceId(id);
                                systemAlarmDto.setSystemAlarmType(SystemAlarmType.SBGZ);
                                try {
                                    rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                deviceId.clear();
                deviceId = null;
            }
        }
        {
            List<Long> tagId = tagExposeService.allId();
            if (Objects.nonNull(tagId) && tagId.size()>=0) {
                tagId.forEach(id -> {
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
                        tagExposeService.updateDeviceDataTime(id, dateTime);
                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
                    }
                });
                tagId.clear();
                tagId = null;
            }
        }
    }
}
