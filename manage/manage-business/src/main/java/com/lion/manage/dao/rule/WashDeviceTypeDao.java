package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.WashDeviceType;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/14 上午10:01
 **/
public interface WashDeviceTypeDao extends BaseDao<WashDeviceType> {

    /**
     * 根据洗手规则来删除
     * @param washId
     * @return
     */
    public int deleteByWashId(Long washId);

    /**
     * 根据洗手规则id查询洗手设备类型
     * @param washId
     * @return
     */
    public List<WashDeviceType> findByWashId(Long washId);
}
