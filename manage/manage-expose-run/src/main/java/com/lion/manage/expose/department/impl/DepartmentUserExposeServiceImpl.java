package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.department.DepartmentUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:29
 */
@DubboService(interfaceClass = DepartmentUserExposeService.class)
public class DepartmentUserExposeServiceImpl extends BaseServiceImpl<DepartmentUser> implements DepartmentUserExposeService {

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public void relationDepartment(Long userId, Long departmentId) {
        departmentUserService.relationDepartment(userId,departmentId);
    }

    @Override
    public Department findDepartment(Long userId) {
        return departmentDao.findByUserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        departmentUserDao.deleteByUserId(userId);
    }
}
