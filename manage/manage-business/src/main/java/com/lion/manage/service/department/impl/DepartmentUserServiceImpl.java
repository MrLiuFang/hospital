package com.lion.manage.service.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.service.department.DepartmentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.management.counter.perf.PerfInstrumentation;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:24
 */
@Service
public class DepartmentUserServiceImpl extends BaseServiceImpl<DepartmentUser> implements DepartmentUserService {

    @Autowired
    private DepartmentUserDao departmentUserDao;
}
