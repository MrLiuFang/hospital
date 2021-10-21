package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:28
 */
@DubboService(interfaceClass = DepartmentExposeService.class)
public class DepartmentExposeServiceImpl extends BaseServiceImpl<Department> implements DepartmentExposeService {

    @Autowired
    private DepartmentService departmentService;

    @DubboReference
    private RoleExposeService roleExposeService;

    @Autowired
    private DepartmentDao departmentDao;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Override
    public List<Long> responsibleDepartment(Long departmentId) {
        return departmentService.responsibleDepartment(departmentId);
    }

//    @Override
//    public Department find(Long deviceGroupId) {
//        return departmentDao.findByDeviceGroupId(deviceGroupId);
//    }
}
