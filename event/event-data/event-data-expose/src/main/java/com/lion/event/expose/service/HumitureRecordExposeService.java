package com.lion.event.expose.service;

import com.lion.core.service.BaseService;
import com.lion.event.entity.HumitureRecord;

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
}
