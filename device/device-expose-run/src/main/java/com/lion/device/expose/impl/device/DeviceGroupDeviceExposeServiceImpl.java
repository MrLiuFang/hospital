package com.lion.device.expose.impl.device;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceGroupDeviceDao;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:29
 */
@DubboService(interfaceClass = DeviceGroupDeviceExposeService.class )
public class DeviceGroupDeviceExposeServiceImpl extends BaseServiceImpl<DeviceGroupDevice> implements DeviceGroupDeviceExposeService {

    @Autowired
    private DeviceGroupDeviceDao deviceGroupDeviceDao;

    @Override
    public List<DeviceGroupDevice> find(Long deviceGroupId) {
        return deviceGroupDeviceDao.findByDeviceGroupId(deviceGroupId);
    }

    @Override
    public DeviceGroupDevice findByDeviceId(Long deviceId) {
        return deviceGroupDeviceDao.findFirstByDeviceId(deviceId);
    }

    @Override
    public Integer countDevice(Long deviceGroupId) {
        return deviceGroupDeviceDao.countByDeviceGroupId(deviceGroupId);
    }
}
