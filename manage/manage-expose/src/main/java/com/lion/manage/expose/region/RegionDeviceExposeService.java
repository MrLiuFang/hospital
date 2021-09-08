package com.lion.manage.expose.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.RegionDevice;

import java.awt.geom.PathIterator;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:48
 */
public interface RegionDeviceExposeService extends BaseService<RegionDevice> {

    public RegionDevice find(Long deviceId);
}
