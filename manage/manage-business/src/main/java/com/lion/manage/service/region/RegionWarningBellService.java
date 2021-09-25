package com.lion.manage.service.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.RegionWarningBell;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:10
 */
public interface RegionWarningBellService extends BaseService<RegionWarningBell> {

    /**
     * 绑定区域和警示铃
     * @param warningBellIds
     * @param regionId
     */
    public void add(List<Long> warningBellIds, Long regionId);

    /**
     * 根据区域查询关联关系
     * @param regionId
     * @return
     */
    public List<RegionWarningBell> find(Long regionId);
}
