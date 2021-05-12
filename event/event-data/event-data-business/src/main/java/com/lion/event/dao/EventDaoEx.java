package com.lion.event.dao;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/11 下午3:08
 **/
public interface EventDaoEx {

    /**
     * 更新解除警告时间
     * @param uuid
     * @param uadt
     */
    public void updateUadt(String uuid, LocalDateTime uadt);

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @param isDepartmentGroup 是否按部门分组统计
     * @param isAlarm 是否触发警告（违规）
     * @param isNoWash 是否错过洗手（uadt = 9999-01-01 00:00:00）
     * @return
     */
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, Boolean isAlarm, Boolean isNoWash);
}
