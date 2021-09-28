package com.lion.manage.dao.department;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.department.DepartmentAlarm;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/27 下午3:49
 */
public interface DepartmentAlarmDao extends BaseDao<DepartmentAlarm> {

    /**
     * 根据部门查询配置
     * @param departmentId
     * @return
     */
    public DepartmentAlarm findByDepartmentId(Long departmentId);
}
