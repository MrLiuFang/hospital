package com.lion.manage.service.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.Ward;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:12
 */
public interface WardService extends BaseService<Ward> {
    /**
     * 根据科室删除
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);
}
