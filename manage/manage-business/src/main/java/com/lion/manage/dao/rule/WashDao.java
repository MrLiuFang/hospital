package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.Wash;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:47
 */
public interface WashDao extends BaseDao<Wash> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public Wash findFirstByName(String name);
}
