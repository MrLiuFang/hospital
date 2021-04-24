package com.lion.device.expose.impl.device;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.device.DeviceExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:25
 */
@DubboService(interfaceClass = DeviceExposeService.class )
public class DeviceExposeServiceImpl extends BaseServiceImpl<Device> implements DeviceExposeService {

    @Autowired
    private DeviceDao deviceDao;

    @Override
    public List<Device> findByDeviceGruopId(Long deviceGroupId) {
        return deviceDao.findByDeviceGroupId(deviceGroupId);
    }

    @Override
    public Device find(String code) {
        return deviceDao.findFirstByCode(code);
    }
}
