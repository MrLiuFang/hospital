package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import com.lion.person.expose.person.PatientExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午2:41
 */
@DubboService(interfaceClass = PatientExposeService.class)
public class PatientExposeServiceImpl extends BaseServiceImpl<Patient> implements PatientExposeService {

    @Autowired
    private PatientDao patientDao;

    @Override
    public void updateState(Long id, Integer state) {
        patientDao.updateState(id, State.instance(state));
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        patientDao.updateLastDataTime(id,dateTime);
    }
}
