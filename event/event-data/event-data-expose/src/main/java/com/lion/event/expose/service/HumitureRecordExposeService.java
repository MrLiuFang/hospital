package com.lion.event.expose.service;

import com.lion.core.service.BaseService;
import com.lion.event.entity.HumitureRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/3 上午9:20
 */
public interface HumitureRecordExposeService  {

    /**
     * 查询最后的记录
     * @param tagId
     * @return
     */
    public HumitureRecord findLast(Long tagId);

    /**
     * 获取指定时间内的记录
     * @param tagId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List< HumitureRecord> find(Long tagId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
