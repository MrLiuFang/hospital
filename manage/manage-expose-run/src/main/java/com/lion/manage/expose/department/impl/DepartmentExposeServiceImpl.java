package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.service.department.DepartmentService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:28
 */
@DubboService(interfaceClass = DepartmentExposeService.class)
public class DepartmentExposeServiceImpl extends BaseServiceImpl<Department> implements DepartmentExposeService {

    @Autowired
    private DepartmentService departmentService;
}
