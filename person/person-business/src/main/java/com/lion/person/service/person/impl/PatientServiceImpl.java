package com.lion.person.service.person.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.tag.TagPatient;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.RestrictedAreaService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:07
 */
@Service
public class PatientServiceImpl extends BaseServiceImpl<Patient> implements PatientService {

    @DubboReference
    private TagPatientExposeService tagPatientExposeService;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private RestrictedAreaService restrictedAreaService;

//    private

    @Override
    @Transactional
    public void add(AddPatientDto addPatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(addPatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),null);
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),null);
        patient = save(patient);
        restrictedAreaService.add(addPatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
    }

    @Override
    @Transactional
    public void update(UpdatePatientDto updatePatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(updatePatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),patient.getId());
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),patient.getId());
        update(patient);
        restrictedAreaService.add(updatePatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtos) {
        if (Objects.nonNull(deleteDtos) ){
            deleteDtos.forEach(deleteDto -> {
                this.deleteById(deleteDto.getId());
                restrictedAreaService.delete(deleteDto.getId());
                tagPatientExposeService.unbinding(deleteDto.getId(),true);
            });
        }
    }

//    private Patient setRegionInfo(Long sickbedId) {
//
//    }


    private void sickbedIsCanUse(Long sickbedId,Long patientId) {
        Patient patient = patientDao.findFirstBySickbedIdAndIsLeave(sickbedId,false);
        if ((Objects.isNull(patientId) && Objects.nonNull(patient)) || (Objects.nonNull(patientId) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),patientId)) ){
            BusinessException.throwException("该床位已有患者未登出,不能使用");
        }
    }

    private void assertMedicalRecordNoExist(String medicalRecordNo, Long id) {
        Patient patient = patientDao.findFirstByMedicalRecordNo(medicalRecordNo);
        if ((Objects.isNull(id) && Objects.nonNull(patient)) || (Objects.nonNull(id) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),id)) ){
            BusinessException.throwException("该病历号已被使用");
        }
    }
}
