package com.lion.device.expose.impl.device;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:29
 */
@DubboService(interfaceClass = DeviceGroupDeviceExposeService.class )
public class DeviceGroupDeviceExposeServiceImpl extends BaseServiceImpl<DeviceGroupDevice> implements DeviceGroupDeviceExposeService {
}
