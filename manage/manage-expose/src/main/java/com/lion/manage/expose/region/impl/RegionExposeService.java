package com.lion.manage.expose.region.impl;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午2:46
 **/
public interface RegionExposeService extends BaseService<Region> {

    /**
     * 根据设备组ID查询关联的区域
     * @param deviceGroupId
     * @return
     */
    public  Region find(Long deviceGroupId);

    /**
     * 删除区域关联的设备组（将deviceGroupId字段置为null）
     * @param deviceGroupId
     */
    public void deleteDeviceGroup(Long deviceGroupId);
}
