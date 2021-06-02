package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.TempLeave;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:23
 */
public interface TempLeaveDao extends BaseDao<TempLeave> {

    /**
     *
     * @param patientId
     * @return
     */
    public TempLeave findFirstByPatientIdOrderByCreateDateTimeDesc(Long patientId);

    /**
     * 查询患者临时离开权限
     * @param patientId
     * @param dateTime
     * @return
     */
    public List<TempLeave> findByPatientIdAndEndDateTimeAfterAndIsClosure(Long patientId,LocalDateTime dateTime,Boolean isClosure);


}
