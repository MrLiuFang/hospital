package com.lion.manage.dao.ward;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.ward.Ward;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:58
 */
public interface WardDao extends BaseDao<Ward> {

    /**
     * 根据科室删除
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 根据科室查询病房基本信息
     * @param departmentId
     * @return
     */
    public List<Ward> findByDepartmentId(Long departmentId);
}
