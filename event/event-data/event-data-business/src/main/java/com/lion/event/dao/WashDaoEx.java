package com.lion.event.dao;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.Wash;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 下午2:59
 **/
public interface WashDaoEx {

    /**
     * 列表
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<Wash>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}