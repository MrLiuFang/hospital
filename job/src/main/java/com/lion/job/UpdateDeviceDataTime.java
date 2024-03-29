package com.lion.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.cctv.CctvExposeService;
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.lion.core.Optional;

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

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        {
            List<Long> userId = userExposeService.allId();
            if (Objects.nonNull(userId) && userId.size()>=0) {
                userId.forEach(id -> {
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
//                        userExposeService.updateDeviceDataTime(id, dateTime);
//                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
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
//                        assetsExposeService.updateDeviceDataTime(id, dateTime);
//                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
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
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime)) {
                        deviceExposeService.updateDeviceDataTime(id, dateTime);
//                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));

                    } else {
                        com.lion.core.Optional<Device> optional = deviceExposeService.findById(id);
                        if (optional.isPresent()) {
                            dateTime = optional.get().getLastDataTime();
                        }
                    }
                    if (Objects.nonNull(dateTime)) {
                        java.time.Duration duration = java.time.Duration.between(dateTime, LocalDateTime.now());
                        if (duration.toMinutes() > (60 * 24)) {
                            Boolean isOffLine = (Boolean) redisTemplate.opsForValue().get(RedisConstants.DEVICE_OFF_LINE+id);
                            if (Objects.isNull(isOffLine)) {
                                SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
                                systemAlarmDto.setDateTime(LocalDateTime.now());
                                systemAlarmDto.setType(Type.DEVICE);
                                Optional<Device> deviceOptional = deviceExposeService.findById(id);
                                if (deviceOptional.isPresent()) {
                                    systemAlarmDto.setRegionId(deviceOptional.get().getRegionId());
                                }
                                systemAlarmDto.setDeviceId(id);
                                systemAlarmDto.setSystemAlarmType(SystemAlarmType.LS);
                                try {
                                    rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                                    redisTemplate.opsForValue().set(RedisConstants.DEVICE_OFF_LINE+id,true,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                            deviceExposeService.updateIsOnline(id,false);
                        }else {
                            deviceExposeService.updateIsOnline(id,true);
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
//                        tagExposeService.updateDeviceDataTime(id, dateTime);
//                        redisTemplate.delete(RedisConstants.LAST_DATA + String.valueOf(id));
                    }
                });
                tagId.clear();
                tagId = null;
            }
        }

        {
            List<Cctv> cctvs = cctvExposeService.findAll();
            cctvs.forEach(cctv -> {
                if (Objects.nonNull(cctv.getIp()) && Objects.nonNull(cctv.getPort())) {
                    Socket socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(cctv.getIp(), cctv.getPort()), 10000); // 建立连接
                        if (socket.isConnected()){
                            cctv.setIsOnline(true);
                        }else {
                            cctv.setIsOnline(false);
                        }
                    }catch (Exception exception) {
                        cctv.setIsOnline(false);
                    }finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    cctvExposeService.update(cctv);
                }
            });
        }
    }
}
