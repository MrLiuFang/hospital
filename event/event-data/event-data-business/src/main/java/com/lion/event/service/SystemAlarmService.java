package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.ListSystemAlarmVo;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmVo;

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
     * 跟据uuid查询
     * @param uuid
     * @return
     */
    public SystemAlarm find(String uuid);

    /**
     * 解除警告
     * @param uuid
     * @param id
     */
    void unalarm(String uuid,String id) throws JsonProcessingException;

    /**
     * 添加汇报
     * @param alarmReportDto
     */
    public void  alarmReport(AlarmReportDto alarmReportDto);

    /**
     * 旧的汇报添加为新的警告
     * @param id
     */
    public void oldAlarmToNewAlarm(String id) throws JsonProcessingException;

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

    /**
     * 警告列表
     * @param lionPage
     * @param departmentIds
     * @param ua
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 查询未处理的告警
     * @param pi
     * @param ai
     * @param dvi
     * @param ti
     * @return
     */
    public SystemAlarm findOne(Long pi, Long ai, Long dvi,Long ti);

    /**
     * 列表
     * @param pi
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListSystemAlarmVo>> list(Long pi, LionPage lionPage);
}
