package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientNurseDao;
import com.lion.person.entity.person.PatientNurse;
import com.lion.person.expose.person.PatientNurseExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午9:32
 */
@DubboService
public class PatientNurseExposeServiceImpl extends BaseServiceImpl<PatientNurse> implements PatientNurseExposeService {

    @Autowired
    private PatientNurseDao patientNurseDao;

    @Override
    public List<PatientNurse> find(Long patientId) {
        return patientNurseDao.findByPatientId(patientId);
    }
}
