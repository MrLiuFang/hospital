package com.lion.event.service;

import com.lion.event.entity.Position;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:12
 **/
public interface PositionService {

    public void save(Position position);

    /**
     * 获取员工指定时间内的行动轨迹
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<Position> find(Long userId , LocalDateTime startDateTime, LocalDateTime endDateTime);
}
