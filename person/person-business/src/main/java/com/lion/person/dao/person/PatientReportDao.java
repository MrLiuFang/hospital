package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.PatientReport;
import org.springframework.context.annotation.Primary;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:36
 */
public interface PatientReportDao extends BaseDao<PatientReport> {

    /**
     * 查询最后的汇报
     * @param patientId
     * @return
     */
    public PatientReport findFirstByPatientIdOrderByCreateDateTimeDesc(Long patientId);
}
