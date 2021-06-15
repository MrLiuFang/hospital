package com.lion.person.service.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.PatientNurseDao;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.entity.person.PatientNurse;
import com.lion.person.service.person.PatientNurseService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午8:57
 */
@Service
public class PatientNurseServiceImpl extends BaseServiceImpl<PatientNurse> implements PatientNurseService {

    @Autowired
    private PatientNurseDao patientNurseDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Override
    @Transactional
    public void add(List<Long> nurseIds, Long patientId) {
        if (Objects.isNull(patientId)){
            return;
        }
        patientNurseDao.deleteByPatientId(patientId);
        nurseIds.forEach(id->{
            assertNurseExist(id);
        });

        nurseIds.forEach(id->{
            PatientNurse patientNurse = new PatientNurse();
            patientNurse.setNurseId(id);
            patientNurse.setPatientId(patientId);
            save(patientNurse);
        });
    }

    @Override
    public List<PatientNurse> find(Long patientId) {
        return patientNurseDao.findByPatientId(patientId);
    }

    private void assertNurseExist(Long nurseId) {
        User user = userExposeService.findById(nurseId);
        if (Objects.isNull(user)){
            BusinessException.throwException("该护士不存在");
        }
        if (!Objects.equals(user.getUserType(), UserType.NURSE)) {
            BusinessException.throwException("选择的负责护士非护士人员");
        }
    }
}
