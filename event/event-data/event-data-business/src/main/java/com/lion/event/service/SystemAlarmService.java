package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.SystemAlarmHandleDto;
import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.*;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:07
 **/
public interface SystemAlarmService {

    public SystemAlarm save(SystemAlarm systemAlarm);

    /**
     * 更新显示排序时间
     * @param id
     */
    public void updateSdt(String id);



    /**
     * 解除警告
     * @param id
     */
    void unalarm(String id) throws JsonProcessingException;

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
     * 查找最后的警告
     * @param pi
     * @return
     */
    public SystemAlarm findLast(Long pi);

    /**
     * 警告列表
     * @param lionPage
     * @param departmentIds
     * @param ua
     * @param ri
     * @param alarmType
     * @param tagIds
     * @param startDateTime
     * @param endDateTime
     * @param tagId
     * @param sorts
     * @return
     */
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds,  LocalDateTime startDateTime,LocalDateTime endDateTime,Long tagId,String... sorts);
    public List<Document> listGroup(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
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
     * @param ri
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListSystemAlarmVo>> list(Long pi,Long ri,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 警告详情
     * @param id
     * @return
     */
    public SystemAlarmDetailsVo details(String id);

    /**
     * 更新警告状态
     * @param systemAlarmDto
     */
    public void updateState(SystemAlarmHandleDto systemAlarmDto);

    /**
     * 7天警告数量统计
     * @param departmentId
     * @return
     */
    List<SevenDaysStatisticsVo> sevenDaysStatistics(Long departmentId);

    /**
     * 今日警报统计
     * @return
     * @param startDateTime
     * @param endDateTime
     */
    public TodayDaysStatisticsVo todayDaysStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime);


}
