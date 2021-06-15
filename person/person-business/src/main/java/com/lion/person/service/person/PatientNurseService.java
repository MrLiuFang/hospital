package com.lion.person.service.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.entity.person.PatientNurse;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午8:57
 */
public interface PatientNurseService extends BaseService<PatientNurse> {

    /**
     * 患者关联护士
     * @param nurseIds
     * @param patientId
     */
    public void add(List<Long> nurseIds, Long patientId);

    /**
     * 根据病人查询
     * @param patientId
     * @return
     */
    public List<PatientNurse> find(Long patientId);
}
