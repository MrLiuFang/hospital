package com.lion.manage.expose.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:27
 */
public interface DepartmentUserExposeService extends BaseService<DepartmentUser> {

    /**
     * 关联用户所在的科室
     * @param userId
     * @param departmentId
     */
    public void relationDepartment(Long userId,Long departmentId);

    /**
     * 获取用户所在的科室
     * @param userId
     * @return
     */
    public Department findDepartment(Long userId);
}
