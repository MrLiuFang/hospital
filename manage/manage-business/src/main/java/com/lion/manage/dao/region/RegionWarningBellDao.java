package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.RegionWarningBell;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteByRegionId(Long regionId);


    /**
     * 根据区域查询关联关系
     * @param regionId
     * @return
     */
    public List<RegionWarningBell> findByRegionId(Long regionId);

    /**
     * 根据警示铃查询关联关系
     * @param warningBellId
     * @return
     */
    public RegionWarningBell findFirstByWarningBellId(Long warningBellId);
}
