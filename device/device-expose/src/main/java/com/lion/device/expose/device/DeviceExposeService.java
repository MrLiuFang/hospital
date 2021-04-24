package com.lion.device.expose.device;

import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.Device;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午8:22
 */
public interface DeviceExposeService extends BaseService<Device> {

    /**
     * 根据设备组查询所有设备
     * @param deviceGroupId
     * @return
     */
    public List<Device> findByDeviceGruopId(Long deviceGroupId);

    /**
     * 根据编码查询设备
     * @param code
     * @return
     */
    public Device find(String code);

}
