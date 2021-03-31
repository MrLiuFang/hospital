package com.lion.device.dao.device;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.device.DeviceGroup;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:43
 */
public interface DeviceGroupDao extends BaseDao<DeviceGroup> {

    /**
     * 根据设备组名称查询
     * @param name
     * @return
     */
    public DeviceGroup findFirstByName(String name);

    /**
     * 根据设备组编号查询
     * @param code
     * @return
     */
    public DeviceGroup findFirstByCode(String code);
}
