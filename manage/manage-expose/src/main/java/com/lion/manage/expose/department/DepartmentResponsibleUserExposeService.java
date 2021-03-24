package com.lion.manage.expose.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.entity.department.vo.ResponsibleUserVo;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24上午11:00
 */
public interface DepartmentResponsibleUserExposeService extends BaseService<DepartmentResponsibleUser> {

    /**
     * 关联科室负责人
     * @param userId
     * @param departmentIds
     */
    public void relationDepartment(Long userId, List<Long> departmentIds);

    /**
     * 获取用户所有负责的科室
     * @param userId
     * @return
     */
    public List<Department> findDepartment(Long userId);

    /**
     * 获取科室负责人
     * @param departmentId
     * @return
     */
    public List<Map<String,Object>> responsibleUser(Long departmentId);
}
