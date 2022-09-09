package com.lion.manage.dao.department;

import com.lion.manage.entity.department.Department;

import java.util.List;

public interface DepartmentDaoEx {

    public List<Department> findAllParent(String name);
}
