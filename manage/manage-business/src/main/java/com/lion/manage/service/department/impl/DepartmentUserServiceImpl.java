package com.lion.manage.service.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.service.department.DepartmentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.management.counter.perf.PerfInstrumentation;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:24
 */
@Service
public class DepartmentUserServiceImpl extends BaseServiceImpl<DepartmentUser> implements DepartmentUserService {

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public int deleteByDepartmentId(Long departmentId) {
        return departmentUserDao.deleteByDepartmentId(departmentId);
    }

    @Override
    public void relationDepartment(Long userId, Long departmentId) {
        departmentUserDao.deleteByUserId(userId);
        if (Objects.nonNull(departmentId)) {
            DepartmentUser departmentUser = new DepartmentUser();
            departmentUser.setUserId(userId);
            departmentUser.setDepartmentId(departmentId);
            this.save(departmentUser);
        }
    }

    @Override
    public Department findDepartment(Long userId) {
        return departmentDao.findByUserId(userId);
    }
}
