package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.RegionDevice;
import org.springframework.context.annotation.Primary;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:50
 */
public interface RegionDeviceDao extends BaseDao<RegionDevice> {

    /**
     * 根据设备查区域
     * @param deviceId
     * @return
     */
    public RegionDevice findFirstByDeviceId(Long deviceId);
}