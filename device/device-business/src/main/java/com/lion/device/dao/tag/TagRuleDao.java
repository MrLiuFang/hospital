package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagRule;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:02
 **/
public interface TagRuleDao extends BaseDao<TagRule> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public TagRule findFirstByName(String name);
}
