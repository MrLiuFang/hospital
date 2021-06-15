package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagRule;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 上午11:32
 */
public interface TagRuleExposeService extends BaseService<TagRule> {

    /**
     * 根据员工查询标签按钮规则
     * @param userId
     * @return
     */
    public TagRule find(Long userId);
}
