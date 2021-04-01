package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.Region;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:56
 */
public interface RegionDao extends BaseDao<Region> {

    /**
     * 根据建筑删除区域
     * @param buildId
     * @return
     */
    public int deleteByBuildId(Long buildId);

    /**
     * 根据科室查询区域
     * @param departmentId
     * @return
     */
    public List<Region> findByDepartmentId(Long departmentId);

    /**
     * 根据建筑查询
     * @param buildId
     * @return
     */
    public List<Region> findByBuildId(Long buildId);

    /**
     * 根据建筑楼层查询
     * @param buildFloorId
     * @return
     */
    public List<Region> findByBuildFloorId(Long buildFloorId);

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public Region findFirstByName(String name);
}
