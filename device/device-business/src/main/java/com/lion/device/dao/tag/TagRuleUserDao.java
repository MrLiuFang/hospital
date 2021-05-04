package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagRuleUser;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:03
 **/
public interface TagRuleUserDao extends BaseDao<TagRuleUser> {

    /**
     * 根据用户和规则id删除
     * @param userId
     * @param tagRuleId
     * @return
     */
    public int deleteByUserIdAndAndTagRuleId(Long userId,Long tagRuleId);

    /**
     * 根据规则id删除
     * @param tagRuleId
     * @return
     */
    public int deleteByTagRuleId(Long tagRuleId);

    /**
     * 根据用户查询关联关系
     * @param userId
     * @param tagRuleId
     * @return
     */
    public TagRuleUser findFirstByUserIdAndTagRuleIdNot(Long userId,Long tagRuleId);
}
