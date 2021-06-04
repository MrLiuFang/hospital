package com.lion.event.dao;

import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:22
 **/
public interface SystemAlarmDaoEx {

    /**
     * 更新显示排序时间
     * @param uuid
     */
    public void updateSdt(String uuid);

    /**
     * 根据UUID 查询
     * @param uuid
     * @return
     */
    public SystemAlarm findUuid(String uuid);

    /**
     * 根就id查询
     * @param id
     * @return
     */
    public SystemAlarm findId(String id);


    /**
     * 解除警告
     * @param uuid
     * @param id
     * @param userId
     * @param userName
     */
    void unalarm(String uuid,String id,Long userId,String userName);

    /**
     * 添加汇报
     * @param alarmReportDto
     * @param userId
     * @param userName
     */
    public void alarmReport(AlarmReportDto alarmReportDto,Long userId, String userName);

    /**
     * 根据区域统计区域的有没有发生警告（24小时内）
     * @param buildFloorId
     * @param map
     * @return
     */
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map);

    /**
     * 根据部门统计警告数量
     * @param departmentId
     * @return
     */
    public Map<String, Integer> groupCount(Long departmentId);

    /**
     * 获取员工指定时间内的警告
     * @param userId
     * @param ua
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<SystemAlarm> find(Long userId, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     *
     * @param lionPage
     * @param departmentIds
     * @param ua
     * @param ri
     * @param alarmType
     * @param tagIds
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, Long ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
