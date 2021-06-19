package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
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

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Override
    public List<Long> responsibleDepartment(Long departmentId) {
        List<Long> departmentIds = new ArrayList<>();
        Long userId = CurrentUserUtil.getCurrentUserId();
        Role role = roleExposeService.find(userId);
        if (Objects.nonNull(role)) {
            if (role.getCode().toLowerCase().indexOf("admin") < 0) {
                List<Department> list = new ArrayList<>();
                if (Objects.nonNull(departmentId)) {
                    list = departmentResponsibleUserExposeService.findDepartment(userId, departmentId);
                } else {
                    list = departmentResponsibleUserExposeService.findDepartment(userId);
                }
                list.forEach(department -> {
                    departmentIds.add(department.getId());
                });
                if (departmentIds.size()<=0) {
                    departmentIds.add(Long.MAX_VALUE);
                }
            } else {
                if (Objects.nonNull(departmentId)) {
                    departmentIds.add(departmentId);
                }else {
                    List<Department> list = findAll();
                    list.forEach(department -> {
                        departmentIds.add(department.getId());
                    });
                }
            }
        }
//        else {
//            departmentIds.add(Long.MAX_VALUE);
//        }
        return departmentIds;
    }
}
