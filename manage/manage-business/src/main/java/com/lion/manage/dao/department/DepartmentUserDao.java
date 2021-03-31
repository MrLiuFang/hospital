package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.department.DepartmentUser;
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
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 删除用户所在的科室
     * @param userId
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public int deleteByUserId(Long userId);

    /**
     * 查询科室关联的所有员工
     * @param departmentId
     * @return
     */
    public List<DepartmentUser> findByDepartmentId(Long departmentId);
}
