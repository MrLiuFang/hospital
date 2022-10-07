package com.lion.person.dao.person;

import com.lion.core.LionPage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午8:47
 */
public interface TempLeaveDaoEx {

    /**
     * 列表
     *
     * @param tagCode
     * @param departmentId
     * @param patientId
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param ids
     * @param lionPage
     * @return
     */
    public Page list(String tagCode, Long departmentId, Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, String ids,LionPage lionPage);
}
