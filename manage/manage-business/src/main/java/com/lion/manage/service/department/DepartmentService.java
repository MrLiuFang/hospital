package com.lion.manage.service.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.dto.AddDepartmentDto;

import java.util.PrimitiveIterator;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:22
 */
public interface DepartmentService extends BaseService<Department> {

    /**
     * 新增科室
     * @param addDepartmentDto
     * @return
     */
    public Department add(AddDepartmentDto addDepartmentDto);

    /**
     * 判断科室名称是否存在
     * @param name
     * @param id
     */
    void assertNameExist(String name,Long id);
}
