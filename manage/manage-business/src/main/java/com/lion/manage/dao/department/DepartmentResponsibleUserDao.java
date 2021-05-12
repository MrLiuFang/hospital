package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:14
 */
public interface DepartmentResponsibleUserDao extends BaseDao<DepartmentResponsibleUser> {

    /**
     * 根据科室ID查询负责人
     * @param departmentId
     * @return
     */
    public List<DepartmentResponsibleUser> findByDepartmentId(Long departmentId);

    /**
     * 根据科室ID删除负责人
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 删除科室负人
     * @param userId
     * @return
     */
    public int deleteByUserId(Long userId);
}
