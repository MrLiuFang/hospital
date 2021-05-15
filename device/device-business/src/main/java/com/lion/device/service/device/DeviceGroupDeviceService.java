package com.lion.device.service.device;

import com.lion.core.service.BaseService;
import com.lion.device.entity.device.DeviceGroupDevice;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:48
 */
public interface DeviceGroupDeviceService extends BaseService<DeviceGroupDevice> {

    /**
     * 根据设备ID删除
     * @param deviceId
     * @return
     */
    public int deleteByDeviceId(Long deviceId);

    /**
     * 根据设备组ID删除
     * @param deviceGroupId
     * @return
     */
    public int deleteByDeviceGroupId(Long deviceGroupId);

    /**
     * 新建设备组关联设备
     * @param deviceGroupId
     * @param deviceId
     * @return
     */
    public int add(Long deviceGroupId, List<Long> deviceId);

    /**
     * 根据设备组id查询所有的设备
     * @param deviceGroupId
     * @return
     */
    public List<DeviceGroupDevice> find(Long deviceGroupId);
}
