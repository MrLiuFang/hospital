package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.TempLeave;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:23
 */
public interface TempLeaveDao extends BaseDao<TempLeave> {

    /**
     * 查询患者临时离开的权限
     * @param isClosure
     * @param patientId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public TempLeave findFirstByIsClosureAndOrPatientIdAndStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualOrderByCreateDateTimeDesc(Boolean isClosure, Long patientId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     *
     * @param patientId
     * @return
     */
    public TempLeave findFirstByPatientIdOrderByCreateDateTimeDesc(Long patientId);
}
