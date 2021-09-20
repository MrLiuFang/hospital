package com.lion.device.expose.device;

import com.lion.core.service.BaseService;
import com.lion.device.entity.device.WarningBell;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:42
 */
public interface WarningBellExposeService extends BaseService<WarningBell> {

    /**
     * 根据区域id查询
     * @param regionId
     * @return
     */
    public List<WarningBell> findByRegionId(Long regionId);
}

