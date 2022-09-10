package com.lion.manage.expose.department;

import com.lion.core.Optional;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:27
 */
public interface DepartmentExposeService extends BaseService<Department> {

    /**
     *  获取负责的部门id admin角色返回空
     *
     * @param departmentId
     * @return
     */
    public List<Long> responsibleDepartment(Long departmentId);

//    /**
//     * 根据设备组查找科室
//     * @param deviceGroupId
//     * @return
//     */
//    public Department find(Long deviceGroupId);

    public Optional<Department> find(String name);

}
