package com.lion.event.dao;

import com.lion.event.entity.TagRecord;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午11:21
 */
public interface TagRecordDaoEx {

    /**
     * 查询最后一条数据(一个月内的数据)
     * @param tagId
     * @return
     */
    public TagRecord find(Long tagId);

}
