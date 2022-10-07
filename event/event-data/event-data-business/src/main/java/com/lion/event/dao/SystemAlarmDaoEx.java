package com.lion.event.dao;

import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.entity.vo.SystemAlarmVo;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午3:22
 **/
public interface SystemAlarmDaoEx {

    /**
     * 更新显示排序时间
     * @param id
     */
    public void updateSdt(String id);

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
     * @param id
     * @param userId
     * @param userName
     */
    void unalarm(String id,Long userId,String userName);

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
     * 查找最后的警告
     * @param pi
     * @return
     */
    public SystemAlarm findLast(Long pi);

    /**
     * @param lionPage
     * @param departmentIds
     * @param ua
     * @param ri
     * @param alarmType
     * @param tagIds
     * @param startDateTime
     * @param endDateTime
     * @param tagId
     * @param assetsId
     * @param ids
     * @param deviceId
     * @param sorts
     * @return
     */
    public IPageResultData<List<SystemAlarmVo>> list(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds, LocalDateTime startDateTime, LocalDateTime endDateTime,Long tagId,Long assetsId, String ids, Long deviceId,String... sorts);

    public List<Document> listGroup(LionPage lionPage, List<Long> departmentIds, Boolean ua, List<Long> ri, Type alarmType, List<Long> tagIds,List<Long> deviceIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
    /**
     * 7天警告数量统计
     * @param departmentId
     * @return
     */
    public List<Document> sevenDaysStatistics(Long departmentId);

    /**
     * 今日警报统计
     *
     * @param type
     * @param startDateTime
     * @param endDateTime
     * @param departmentIds
     * @return
     */
    public long todayDaysStatistics(Type type, LocalDateTime startDateTime, LocalDateTime endDateTime,List<Long> departmentIds);

    /**
     * 查找最后一次资产警告
     * @param assetsId
     * @return
     */
    public SystemAlarm findLastByAssetsId(Long  assetsId);

    /**
     * 查找最后一次tag警告
     * @param tagId
     * @return
     */
    public SystemAlarm findLastByTagId(Long  tagId);
}
