package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientReport;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 下午2:52
 */
public interface PatientReportExposeService extends BaseService<PatientReport> {
    /**
     * 查询最后的汇报
     * @param patientId
     * @return
     */
    public PatientReport findLast(Long patientId);
}
