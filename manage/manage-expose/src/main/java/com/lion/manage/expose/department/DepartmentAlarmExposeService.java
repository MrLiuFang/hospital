package com.lion.manage.expose.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.DepartmentAlarm;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/28 下午3:41
 */
public interface DepartmentAlarmExposeService extends BaseService<DepartmentAlarm> {

    /**
     * 根据部门查询配置
     * @param departmentId
     * @return
     */
    public DepartmentAlarm find(Long departmentId);
}
