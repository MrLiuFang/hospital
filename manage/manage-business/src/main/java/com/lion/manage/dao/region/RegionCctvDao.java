package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.RegionCctv;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:57
 */
public interface RegionCctvDao extends BaseDao<RegionCctv> {

    /**
     * 根据区域删除
     * @param regionId
     * @return
     */
    public int deleteByRegionId(Long regionId);

    /**
     * 根据区域查询所有关联的cctv
     * @param regionId
     * @return
     */
    public List<RegionCctv> findByRegionId(Long regionId);

    /**
     * 根据区域统计
     * @param regionId
     * @return
     */
    public int countByRegionId(Long regionId);

    /**
     *
     * @param regionId
     * @return
     */
    public RegionCctv findFirstByCctvId(Long regionId);

}
