package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentAlarmDao;
import com.lion.manage.entity.department.DepartmentAlarm;
import com.lion.manage.expose.department.DepartmentAlarmExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/28 下午3:44
 */
@DubboService(interfaceClass = DepartmentAlarmExposeService.class)
public class DepartmentAlarmExposeServiceImpl extends BaseServiceImpl<DepartmentAlarm> implements DepartmentAlarmExposeService {

    @Autowired
    private DepartmentAlarmDao departmentAlarmDao;

    @Override
    public DepartmentAlarm find(Long departmentId) {
        return departmentAlarmDao.findByDepartmentId(departmentId);
    }
}
