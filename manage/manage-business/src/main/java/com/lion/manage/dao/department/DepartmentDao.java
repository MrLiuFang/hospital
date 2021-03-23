package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.department.Department;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:24
 */
public interface DepartmentDao extends BaseDao<Department> {

    /**
     * 根据科室名称查询科室
     * @param name
     * @return
     */
    public Department findFirstByName(String name);
}
