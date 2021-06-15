package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientDoctorDao;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.expose.person.PatientDoctorExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午9:31
 */
@DubboService
public class PatientDoctorExposeServiceImpl extends BaseServiceImpl<PatientDoctor> implements PatientDoctorExposeService {

    @Autowired
    private PatientDoctorDao patientDoctorDao;

    @Override
    public List<PatientDoctor> find(Long patientId) {
        return patientDoctorDao.findByPatientId(patientId);
    }
}
