package com.lion.person.service.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientDoctor;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午8:44
 */
public interface PatientDoctorService extends BaseService<PatientDoctor> {

    /**
     * 患者关联医生
     * @param doctorIds
     * @param patientId
     */
    public void add(List<Long> doctorIds, Long patientId);

    /**
     * 根据病人查询
     * @param patientId
     * @return
     */
    public List<PatientDoctor> find(Long patientId);
}
