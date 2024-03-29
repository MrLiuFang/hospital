package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.department.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:24
 */
public interface DepartmentDao extends BaseDao<Department> ,DepartmentDaoEx{

    /**
     * 根据科室名称查询科室
     * @param name
     * @return
     */
    public Department findFirstByName(String name);

    /**
     * 根据父节点ID查询子节点
     * @param parentId
     * @return
     */
    public List<Department> findByParentIdOrderByCreateDateTimeAsc(Long parentId);

    public List<Department> findByParentIdAndIdInOrderByCreateDateTimeAsc(Long parentId,List<Long> ids);

    /**
     * 根据科室名称查询科室
     * @param name
     * @return
     */
    public List<Department> findByNameLike(String name);

    /**
     * 获取用户所在的科室
     * @param userId
     * @return
     */
    @Query(" select d from Department d join DepartmentUser du on d.id = du.departmentId where du.userId = :userId ")
    public Department findByUserId(Long userId);

    /**
     * 获取用户所有负责的科室
     * @param userId
     * @return
     */
    @Query(" select d from Department d join DepartmentResponsibleUser dru on d.id = dru.departmentId where dru.userId = :userId ")
    public List<Department> findResponsibleDepartmentByUserId(Long userId);

    /**
     * 获取用户所有负责的科室
     * @param userId
     * @return
     */
    @Query(" select d from Department d join DepartmentResponsibleUser dru on d.id = dru.departmentId where dru.userId = :userId and d.id =:departmentId ")
    public List<Department> findResponsibleDepartmentByUserId(@Param("userId") Long userId,@Param("departmentId")  Long departmentId);

    /**
     * 根据id查询科室
     * @param ids
     * @return
     */
    public List<Department> findByIdIn(List<Long> ids);
    /**
//     * 根据设备组查找科室
//     * @param deviceGroupId
//     * @return
//     */
//    @Query( " select d from Department d join Region r on d.id = r.departmentId where r.deviceGroupId = :deviceGroupId " )
//    public Department findByDeviceGroupId(@Param("deviceGroupId")Long deviceGroupId);

    public Optional<Department> findByName(String name);
}
