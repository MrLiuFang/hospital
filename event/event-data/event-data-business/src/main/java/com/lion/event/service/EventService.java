package com.lion.event.service;

import com.lion.event.entity.Event;
import com.lion.event.entity.vo.WashMonitorVo;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:11
 **/
public interface EventService {

    /**
     * 保存事件
     * @param event
     */
    public void save(Event event);

    /**
     * 更新解除警告时间
     * @param uuid
     * @param uadt
     */
    public void updateUadt(String uuid, LocalDateTime uadt );

    /**
     * 手卫生监控
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public WashMonitorVo eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
