package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.TagRuleLogType;
import com.lion.device.entity.tag.TagRuleLog;
import com.lion.device.entity.tag.vo.ListTagRuleLogVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:04
 **/
public interface TagRuleLogService extends BaseService<TagRuleLog> {

    /**
     * 新增记录
     * @param tagRuleId
     * @param content
     * @param tagRuleLogType
     */
    public void add(Long tagRuleId, String content, TagRuleLogType tagRuleLogType);

    /**
     *
     *
     * @param tagRuleId
     * @param startDateTime
     * @param endDateTime
     * @param tagRuleLogType
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListTagRuleLogVo>> list(Long tagRuleId,LocalDateTime startDateTime, LocalDateTime endDateTime, TagRuleLogType tagRuleLogType, LionPage lionPage);
}
