package com.lion.event.dao;

import com.lion.event.entity.HumitureRecord;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午11:21
 */
public interface HumitureRecordDaoEx {

    /**
     * 查询最后一条数据(一个月内的数据)
     * @param tagId
     * @return
     */
    public HumitureRecord find(Long tagId);

}
