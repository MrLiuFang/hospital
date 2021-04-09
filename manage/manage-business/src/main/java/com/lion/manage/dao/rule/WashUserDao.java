package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.WashUser;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:48
 */
public interface WashUserDao extends BaseDao<WashUser> {
    /**
     * 根据洗手规则删除
     * @param washId
     * @return
     */
    public int deleteByWashId(Long washId);

    /**
     * 根据洗手规则查询
     * @param washId
     * @return
     */
    public List<WashUser> findByWashId(Long washId);
}
