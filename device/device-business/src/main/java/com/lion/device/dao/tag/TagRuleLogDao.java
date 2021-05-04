package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagRuleLog;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:02
 **/
public interface TagRuleLogDao extends BaseDao<TagRuleLog> {

    /**
     * 根据规则id删除
     * @param tagRuleId
     * @return
     */
    public int deleteByTagRuleId(Long tagRuleId);
}
