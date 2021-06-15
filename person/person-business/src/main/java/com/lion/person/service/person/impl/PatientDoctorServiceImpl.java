package com.lion.person.service.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.PatientDoctorDao;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.entity.person.PatientNurse;
import com.lion.person.service.person.PatientDoctorService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午8:59
 */
@Service
public class PatientDoctorServiceImpl extends BaseServiceImpl<PatientDoctor> implements PatientDoctorService {

    @Autowired
    private PatientDoctorDao patientDoctorDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Override
    public void add(List<Long> doctorIds, Long patientId) {
        if (Objects.isNull(patientId)){
            return;
        }
        patientDoctorDao.deleteByPatientId(patientId);
        doctorIds.forEach(id->{
            assertDoctorExist(id);
        });

        doctorIds.forEach(id->{
            PatientDoctor patientDoctor = new PatientDoctor();
            patientDoctor.setDoctorId(id);
            patientDoctor.setPatientId(patientId);
            save(patientDoctor);
        });
    }

    @Override
    public List<PatientDoctor> find(Long patientId) {
        return patientDoctorDao.findByPatientId(patientId);
    }

    private void assertDoctorExist(Long doctorId) {
        User user = userExposeService.findById(doctorId);
        if (Objects.isNull(user)){
            BusinessException.throwException("该医生不存在");
        }
        if (!Objects.equals(user.getUserType(), UserType.DOCTOR)) {
            BusinessException.throwException("选择的负责医生非医生人员");
        }
    }
}
