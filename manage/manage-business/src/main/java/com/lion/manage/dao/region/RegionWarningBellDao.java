package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.RegionWarningBell;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:10
 */
public interface RegionWarningBellDao extends BaseDao<RegionWarningBell> {

    /**
     * 查询警示铃关联关系
     * @param warningBellId
     * @param regionId
     * @return
     */
    public RegionWarningBell findFirstByWarningBellIdAndRegionIdNot(Long warningBellId,Long regionId);

    /**
     * 根据区域删除
     * @param regionId
     * @return
     */
    public int deleteByRegionId(Long regionId);
}
