package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientLogDao;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.person.PatientLog;
import com.lion.person.expose.person.PatientLogExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/19 下午4:39
 */
@DubboService(interfaceClass = PatientLogExposeService.class)
public class PatientLogExposeServiceImpl extends BaseServiceImpl<PatientLog> implements PatientLogExposeService {

    @Autowired
    private PatientLogDao patientLogDao;

    @Override
    public void add(String content, LogType logType, Long operationUserId, Long patientId) {
        PatientLog patientLog = new PatientLog();
        patientLog.setContent(content);
        patientLog.setLogType(logType);
        patientLog.setOperationUserId(operationUserId);
        patientLog.setPatientId(patientId);
        save(patientLog);
    }
}
