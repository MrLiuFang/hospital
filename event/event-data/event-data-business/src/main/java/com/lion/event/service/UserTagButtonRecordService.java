package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.enums.TagRuleEffect;
import com.lion.event.entity.UserTagButtonRecord;
import com.lion.event.entity.vo.ListUserTagButtonRecordVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:35
 */
public interface UserTagButtonRecordService  {

    public void add(UserTagButtonRecord userTagButtonRecord);

    /**
     * 列表
     * @param tagRuleEffect
     * @param name
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListUserTagButtonRecordVo>> list( TagRuleEffect tagRuleEffect,String name,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 导出
     *
     * @param tagRuleEffect
     * @param name
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     */
    public void export( TagRuleEffect tagRuleEffect,String name,LocalDateTime startDateTime,LocalDateTime endDateTime,LionPage lionPage) throws IOException, IllegalAccessException;

    public UserTagButtonRecord findLsat(Long userId);
}
