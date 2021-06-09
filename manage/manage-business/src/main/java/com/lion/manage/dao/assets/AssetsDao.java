package com.lion.manage.dao.assets;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.State;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:12
 */
public interface AssetsDao extends BaseDao<Assets> ,AssetsDaoEx {

    /**
     * 根据名称查询资产
     * @param name
     * @return
     */
    public Assets findFirstByName(String name);

    /**
     * 根据编码查询资产
     * @param code
     * @return
     */
    public Assets findFirstByCode(String code);


    /**
     * 根据编码查询资产
     * @param code
     * @return
     */
    public List<Assets> findByCodeLike(String code);

    /**
     * 根据楼层统计区域内资产数量
     * @param buildFloorId
     * @return
     */
    @Query( "select new map(regionId as region_id, count(id) as count) from Assets where buildFloorId = :buildFloorId group by regionId " )
    public List<Map<String, Object>> groupRegionCount(Long buildFloorId);

    /**
     * 统计科室内的资产数量
     * @param departmentId
     * @return
     */
    public int countByDepartmentId(Long departmentId);

    /**
     * 统计科室内的资产数量
     * @param departmentId
     * @param deviceState
     * @return
     */
    public int countByDepartmentIdAndDeviceSate(Long departmentId,State deviceState);

    /**
     * 查询部门内的资产
     * @param departmentId
     * @return
     */
    public List<Assets> findByDepartmentId(Long departmentId);

    /**
     * 查询部门内的资产
     * @param departmentIds
     * @return
     */
    public List<Assets> findByDepartmentIdIn(List<Long> departmentIds);

    /**
     * 查询部门内的资产
     * @param departmentId
     * @param name
     * @param code
     * @return
     */
    @Query( " select a from Assets a where a.departmentId =:departmentId and ( a.name like :name or a.code like :code) " )
    public List<Assets> findByDepartmentIdOrNameLikeOrCodeLike(Long departmentId,String name,String code);

    @Query( " select a.id from Assets a ")
    public List<Long> allId();

    @Modifying
    @Query(" update Assets  set deviceSate =:state where id = :id ")
    @Transactional
    public void updateSate(@Param("id")Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update Assets  set lastDataTime =:dateTime where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);

}
