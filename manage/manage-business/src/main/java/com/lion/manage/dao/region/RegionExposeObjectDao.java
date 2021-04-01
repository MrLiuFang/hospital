package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionExposeObject;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:57
 */
public interface RegionExposeObjectDao extends BaseDao<RegionExposeObject> {
    /**
     * 根据区域删除
     * @param regionId
     * @return
     */
    public int deleteByRegionId(Long regionId);

    /**
     * 根据区域查询
     * @param regionId
     * @return
     */
    public List<RegionExposeObject> findByRegionId(Long regionId);
}
