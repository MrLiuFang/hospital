package com.lion.event.service;

import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.vo.RegionStatisticsDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
public interface SystemAlarmService {

    public void save(SystemAlarm systemAlarm);

    /**
     * 更新显示排序时间
     * @param uuid
     */
    public void updateSdt(String uuid);

    /**
     * 解除警告
     * @param uuid
     */
    void unalarm(String uuid);

    /**
     * 根据区域统计区域的有没有发生警告（24小时内）
     * @param buildFloorId
     * @param map
     * @return
     */
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map);

    /**
     * 根据科室统计警告数量
     * @param departmentId
     * @return
     */
    public Map<String, Integer> groupCount(Long departmentId);

    /**
     * 查询员工指定时间的警告
     * @param userId
     * @param ua 是否已处理
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<SystemAlarm> find(Long userId ,Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
