package com.lion.device.expose.device;

import com.lion.core.service.BaseService;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.entity.enums.State;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午8:23
 */
public interface DeviceGroupExposeService extends BaseService<DeviceGroup> {

    /**
     * 统计
     * @param departmentId
     * @return
     */
    public int count(Long departmentId);

    /**
     * 统计
     * @param departmentId
     * @return
     */
    public int count(Long departmentId, State state);
}
