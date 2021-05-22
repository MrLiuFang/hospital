package com.lion.event.dao;

import com.lion.event.entity.Position;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午5:10
 */
public interface PositionDaoEx {

    /**
     * 获取员工指定时间内的行动轨迹
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<Position> find(Long userId , LocalDateTime startDateTime, LocalDateTime endDateTime);
}
