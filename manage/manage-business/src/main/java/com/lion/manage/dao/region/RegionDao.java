package com.lion.manage.dao.region;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.region.Region;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * 根据设备组id查询区域
     * @param deviceGroupId
     * @return
     */
    public Region findFirstByDeviceGroupId(Long deviceGroupId);

    /**
     * 删除区域关联的设备组（将deviceGroupId字段置为null）
     * @param deviceGroupId
     */
    @Query(" update Region set deviceGroupId = null where deviceGroupId = :deviceGroupId ")
    public void deleteDeviceGroup(Long deviceGroupId);
}
