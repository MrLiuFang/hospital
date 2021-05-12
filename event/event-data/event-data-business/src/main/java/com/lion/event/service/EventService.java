package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.Event;
import com.lion.event.entity.vo.UserWashDetailsVo;
import com.lion.event.entity.vo.ListUserWashMonitorVo;
import com.lion.event.entity.vo.ListWashMonitorVo;
import com.lion.upms.entity.enums.UserType;

import java.time.LocalDateTime;
import java.util.List;

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
    public ListWashMonitorVo washRatio(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 手卫生监控(低于标准人员)
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListUserWashMonitorVo>> userWashRatio(UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

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
     * @param departmentId
     * @param userType
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(String userName, Long departmentId,  UserType userType,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

}
