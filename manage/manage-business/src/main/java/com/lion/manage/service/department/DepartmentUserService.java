package com.lion.manage.service.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:23
 */
public interface DepartmentUserService extends BaseService<DepartmentUser> {

    /**
     * 删除科室关联员工
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 关联科室
     * @param userId
     * @param departmentId
     */
    public void relationDepartment(Long userId, Long departmentId);

    /**
     * 获取用户所在的科室
     * @param userId
     * @return
     */
    public Department findDepartment(Long userId);
}
