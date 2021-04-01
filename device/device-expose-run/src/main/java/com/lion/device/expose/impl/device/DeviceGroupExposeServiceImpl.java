package com.lion.device.expose.impl.device;

import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:27
 */
@DubboService(interfaceClass = DeviceExposeService.class )
public class DeviceGroupExposeServiceImpl extends BaseServiceImpl<DeviceGroup> implements DeviceGroupExposeService {
}
