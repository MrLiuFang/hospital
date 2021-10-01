package com.lion.person.dao.person;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.vo.ListTempLeaveVo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午8:47
 */
public interface TempLeaveDaoEx {

    /**
     * 列表
     * @param tagCode
     * @param departmentId
     * @param patientId
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public Page list(String tagCode, Long departmentId, Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
