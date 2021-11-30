package com.lion.event.dao;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.WashEvent;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/11 下午3:08
 **/
public interface WashEventDaoEx {

    /**
     * 更新解除事件洗手事件
     * @param uuid
     * @param wt
     */
    public void updateWt(String uuid, LocalDateTime wt);

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @param isDepartmentGroup
     * @param userTypeId
     * @param userId
     * @param lionPage
     * @return
     */
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, Long userTypeId,Long userId, LionPage lionPage);

    /**
     * 区域统计合规率
     * @param startDateTime
     * @param endDateTime
     * @param regionId
     * @return
     */
    public Document eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Long regionId);

    /**
     * 用户洗手详情
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public List<WashEvent> userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 员工合规率-筛选后
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<WashEvent>> userWashConformanceRatioScreen(String userName, List<Long> departmentIds, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 员工合规率-筛选后-百分比
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public Integer userWashConformanceRatioScreenPercentage(String userName, List<Long> departmentIds, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
