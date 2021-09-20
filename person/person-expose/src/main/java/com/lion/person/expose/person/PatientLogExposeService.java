package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.person.PatientLog;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/19 下午4:39
 */
public interface PatientLogExposeService extends BaseService<PatientLog> {

    /**
     * 添加日志
     * @param content
     * @param logType
     * @param operationUserId
     * @param patientId
     */
    public void add(String content, LogType logType, Long operationUserId, Long patientId);
}
