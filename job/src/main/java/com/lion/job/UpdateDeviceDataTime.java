package com.lion.job;

import com.lion.common.constants.RedisConstants;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        {
            List<Long> userId = userExposeService.allId();
            userId.forEach(id->{
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA+String.valueOf(id));
                if (Objects.nonNull(dateTime)) {
                    userExposeService.updateDeviceDataTime(id,dateTime);
                    redisTemplate.delete(RedisConstants.LAST_DATA+String.valueOf(id));
                }
            });
            userId.clear();
            userId = null;
        }
        {
            List<Long> assetsId = assetsExposeService.allId();
            assetsId.forEach(id->{
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA+String.valueOf(id));
                if (Objects.nonNull(dateTime)) {
                    assetsExposeService.updateDeviceDataTime(id,dateTime);
                    redisTemplate.delete(RedisConstants.LAST_DATA+String.valueOf(id));
                }
            });
            assetsId.clear();
            assetsId = null;
        }
        {
            List<Long> deviceId = deviceExposeService.allId();
            deviceId.forEach(id->{
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA+String.valueOf(id));
                if (Objects.nonNull(dateTime)) {
                    deviceExposeService.updateDeviceDataTime(id,dateTime);
                    redisTemplate.delete(RedisConstants.LAST_DATA+String.valueOf(id));
                }
            });
            deviceId.clear();
            deviceId = null;
        }
        {
            List<Long> tagId = tagExposeService.allId();
            tagId.forEach(id->{
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA+String.valueOf(id));
                if (Objects.nonNull(dateTime)) {
                    tagExposeService.updateDeviceDataTime(id,dateTime);
                    redisTemplate.delete(RedisConstants.LAST_DATA+String.valueOf(id));
                }
            });
            tagId.clear();
            tagId = null;
        }
    }
}
