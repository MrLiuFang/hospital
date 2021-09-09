package com.lion.event.dao;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.WashRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 下午2:59
 **/
public interface WashRecordDaoEx {

    /**
     * 列表
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<WashRecord>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
