package com.lion.device.expose.impl.device;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.service.device.DeviceService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:25
 */
@DubboService(interfaceClass = DeviceExposeService.class )
public class DeviceExposeServiceImpl extends BaseServiceImpl<Device> implements DeviceExposeService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Device> findByDeviceGruopId(Long deviceGroupId) {
        return deviceDao.findByDeviceGroupId(deviceGroupId);
    }

    @Override
    public Device find(String code) {
        return deviceDao.findFirstByCode(code);
    }

    @Override
    public void updateBattery(Long deviceId, Integer battery) {
        Device device = deviceService.findById(deviceId);
        if (Objects.nonNull(device)) {
            device.setBattery(battery);
            update(device);
            redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+device.getCode(),device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.DEVICE+device.getId(),device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
    }

    @Override
    public Integer countDevice(List<Long> deviceGroupIds, Integer battery) {
        return deviceDao.countDevice(deviceGroupIds, battery);
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        deviceDao.update(id,dateTime);
    }

    @Override
    public List<Long> allId() {
        return deviceDao.allId();
    }
}
