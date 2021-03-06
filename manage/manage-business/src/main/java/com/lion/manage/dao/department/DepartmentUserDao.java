package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.upms.entity.enums.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:25
 */
public interface DepartmentUserDao extends BaseDao<DepartmentUser> {

    /**
     * 删除科室关联人员
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 删除用户所在的科室
     * @param userId
     * @return
     */
    public int deleteByUserId(Long userId);

    /**
     * 查询科室关联的所有员工
     * @param departmentId
     * @return
     */
    public List<DepartmentUser> findByDepartmentId(Long departmentId);

    /**
     * 查询科室关联的所有员工
     * @param departmentId
     * @param userIds
     * @return
     */
    public List<DepartmentUser> findByDepartmentIdAndUserIdIn(Long departmentId,List<Long> userIds);

    /**
     * 统计部门下面有多少员工
     * @param departmentId
     * @return
     */
    public Integer countByDepartmentId(Long departmentId);

    /**
     * 统计部门下面有多少员工
     * @param departmentId
     * @return
     */
    public Integer countByDepartmentIdAndUserIdIn(Long departmentId,List<Long> userIds);

    /**
     * 获取员工所在的科室
     * @param userId
     * @return
     */
    public DepartmentUser findFirstByUserId(Long userId);

}
