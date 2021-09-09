package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagRule;
import org.springframework.data.jpa.repository.Query;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:02
 **/
public interface TagRuleDao extends BaseDao<TagRule> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public TagRule findFirstByName(String name);

    /**
     * 根据员工查询规则
     * @param userId
     * @return
     */
    @Query( " select t from TagRule t join TagRuleUser tru on t.id = tru.tagRuleId where tru.userId = :userId " )
    public TagRule findByUserId(Long userId);
}
