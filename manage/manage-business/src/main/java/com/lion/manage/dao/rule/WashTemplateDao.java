package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.WashTemplate;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:51
 */
public interface WashTemplateDao extends BaseDao<WashTemplate> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public WashTemplate findFirstByName(String name);
}
