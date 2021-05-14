package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.WashDeviceType;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/14 上午10:01
 **/
public interface WashDeviceTypeDao extends BaseDao<WashDeviceType> {

    /**
     * 根据洗手规则来删除
     * @param washId
     * @return
     */
    public int deleteByWashId(Long washId);
}
