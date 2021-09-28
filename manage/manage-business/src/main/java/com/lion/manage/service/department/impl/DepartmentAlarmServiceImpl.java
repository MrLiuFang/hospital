package com.lion.manage.service.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentAlarmDao;
import com.lion.manage.entity.department.DepartmentAlarm;
import com.lion.manage.service.department.DepartmentAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/27 下午3:59
 */
@Service
public class DepartmentAlarmServiceImpl extends BaseServiceImpl<DepartmentAlarm> implements DepartmentAlarmService {

    @Autowired
    private DepartmentAlarmDao departmentAlarmDao;

    @Override
    public DepartmentAlarm find(Long departmentId) {
        return departmentAlarmDao.findByDepartmentId(departmentId);
    }
}
