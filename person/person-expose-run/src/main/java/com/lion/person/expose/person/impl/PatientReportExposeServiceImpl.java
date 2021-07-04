package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientReportDao;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.expose.person.PatientReportExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 下午2:53
 */
@DubboService
public class PatientReportExposeServiceImpl extends BaseServiceImpl<PatientReport> implements PatientReportExposeService {

    @Autowired
    private PatientReportDao patientReportDao;

    @Override
    public PatientReport findLast(Long patientId) {
        return patientReportDao.findFirstByPatientIdOrderByCreateDateTimeDesc(patientId);
    }
}
