package com.lion.manage.dao.template;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.template.SearchTemplate;

public interface SearchTemplateDao extends BaseDao<SearchTemplate> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public SearchTemplate findFirstByName(String name);
}
