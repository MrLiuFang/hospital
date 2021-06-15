package com.lion.device.expose.impl.tag;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagRuleDao;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.expose.tag.TagRuleExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 上午11:53
 */
@DubboService
public class TagRuleExposeServiceImpl extends BaseServiceImpl<TagRule> implements TagRuleExposeService {

    @Autowired
    private TagRuleDao tagRuleDao;

    @Override
    public TagRule find(Long userId) {
        return tagRuleDao.findByUserId(userId);
    }
}
