package com.lion.event.service;

import com.itextpdf.text.DocumentException;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.WashEventType;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.vo.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:11
 **/
public interface WashEventService {

    /**
     * 保存事件
     * @param washEvent
     */
    public void save(WashEvent washEvent);

    /**
     * 更新洗手时间
     * @param uuid
     * @param uadt
     */
    public void updateWt(String uuid, LocalDateTime uadt );

    /**
     * 手卫生监控
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public ListWashMonitorVo washRatio(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 手卫生监控(低于标准人员)
     *
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListUserWashMonitorVo>> userWashRatio(Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 用户洗手记录详情
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    UserWashDetailsVo userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 员工合规率
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(String userName,List<Long> departmentIds, List<Long> userIds,  List<Long> userTypeId,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     *  员工合规率-率选后
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListWashEventVo1>> userWashConformanceRatioScreen(String userName,List<Long> departmentIds, List<Long> userIds,  List<Long> userTypeId,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 员工合规率-率选后-百分比
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    Integer userWashConformanceRatioScreenPercentage(String userName,List<Long> departmentIds, List<Long> userIds,  List<Long> userTypeId,LocalDateTime startDateTime,LocalDateTime endDateTime);

    /**
     * 员工合规率导出
     * @param userName
     * @param departmentIds
     * @param userIds
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     */
    void userWashConformanceRatioExport(String userName, List<Long> departmentIds, List<Long> userIds,  List<Long> userTypeId,LocalDateTime startDateTime,LocalDateTime endDateTime) throws DocumentException, IOException;

    /**
     * 手卫生行为包报表
     * @param ia
     * @param userTypeId
     * @param type
     * @param regionId
     * @param departmentId
     * @param userIds
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListWashEventVo>> listWashEvent(Boolean ia,Long userTypeId,  WashEventType type,Long regionId,Long departmentId,List<Long> userIds,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 手卫生行为包报表导出
     * @param ia
     * @param type
     * @param regionId
     * @param departmentId
     * @param userIds
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     */
    public void listWashEventExport(Boolean ia,  WashEventType type,Long regionId,Long departmentId,List<Long> userIds,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException;

    /**
     * 手卫生行为包报表区域
     * @param buildFloorId
     * @param regionId
     * @param departmentId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListWashEventRegionVo>> washEventRegionRatio(Long buildFloorId, Long regionId, Long departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 手卫生行为包报表区域导出
     * @param buildFloorId
     * @param regionId
     * @param departmentId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     */
    public void washEventRegionRatioExport(Long buildFloorId, Long regionId, Long departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException;

    /**
     * 更新洗手时长
     * @param userLastWashDto
     */
    public void updateWashTime(UserLastWashDto userLastWashDto);

    /**
     * 不合规洗手事件
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListViolationWashEventVo>> violationWashEvent(LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 统计事件数量
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    long count(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
