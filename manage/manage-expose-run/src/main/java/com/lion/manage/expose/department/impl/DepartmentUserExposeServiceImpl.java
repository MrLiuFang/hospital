package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseExposeServiceImpl;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:29
 */
@DubboService(interfaceClass = DepartmentUserExposeService.class)
public class DepartmentUserExposeServiceImpl extends BaseExposeServiceImpl<DepartmentUser> implements DepartmentUserExposeService {

    @Autowired
    private DepartmentUserDao departmentUserDao;
}
