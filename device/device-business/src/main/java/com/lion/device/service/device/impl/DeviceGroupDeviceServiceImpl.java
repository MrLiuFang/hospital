package com.lion.device.service.device.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.dao.device.DeviceGroupDeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.service.device.DeviceService;
import com.lion.exception.BusinessException;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:49
 */
@Service
public class DeviceGroupDeviceServiceImpl extends BaseServiceImpl<DeviceGroupDevice> implements DeviceGroupDeviceService {

    @Autowired
    private DeviceGroupDeviceDao deviceGroupDeviceDao;

    @Autowired
    private DeviceService deviceService;

    @Override
    public int deleteByDeviceId(Long deviceId) {
        return deviceGroupDeviceDao.deleteByDeviceId(deviceId);
    }

    @Override
    public int deleteByDeviceGroupId(Long deviceGroupId) {
        return deviceGroupDeviceDao.deleteByDeviceGroupId(deviceGroupId);
    }

    @Override
    public int add(Long deviceGroupId, List<Long> deviceIds) {
        if (Objects.nonNull(deviceGroupId)){
            deviceGroupDeviceDao.deleteByDeviceGroupId(deviceGroupId);
        }
        List<DeviceGroupDevice> list = new ArrayList<DeviceGroupDevice>();
        if (Objects.nonNull(deviceIds) && deviceIds.size()>0) {
            deviceIds.forEach(id -> {
                DeviceGroupDevice tmp = deviceGroupDeviceDao.findFirstByDeviceId(id);
                if (Objects.nonNull(tmp) && !Objects.equals(tmp.getDeviceGroupId(),deviceGroupId)){
                    com.lion.core.Optional<Device> optional = deviceService.findById(id);
                    if (optional.isPresent()) {
                        BusinessException.throwException(optional.get().getName() + MessageI18nUtil.getMessage("4000035"));
                    }
                }
                DeviceGroupDevice deviceGroupDevice = new DeviceGroupDevice();
                deviceGroupDevice.setDeviceGroupId(deviceGroupId);
                deviceGroupDevice.setDeviceId(id);
                save(deviceGroupDevice);

            });
        }
        return 0;
    }

    @Override
    public List<DeviceGroupDevice> find(Long deviceGroupId) {
        return deviceGroupDeviceDao.findByDeviceGroupId(deviceGroupId);
    }
}
