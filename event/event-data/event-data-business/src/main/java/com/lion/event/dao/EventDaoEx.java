package com.lion.event.dao;

import com.lion.core.LionPage;
import com.lion.event.entity.Event;
import com.lion.upms.entity.enums.UserType;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/11 下午3:08
 **/
public interface EventDaoEx {

    /**
     * 更新解除警告时间
     * @param uuid
     * @param uadt
     */
    public void updateUadt(String uuid, LocalDateTime uadt);

    /**
     *
     * @param startDateTime
     * @param endDateTime
     * @param isDepartmentGroup
     * @param userType
     * @param userId
     * @param lionPage
     * @return
     */
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, UserType userType,Long userId, LionPage lionPage);

    /**
     * 用户洗手详情
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public List<Event> userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
