package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.Patient;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:03
 */
public interface PatientDao extends BaseDao<Patient> {

    /**
     * 根据床位查询患者
     * @param sickbedId
     * @param isLeave
     * @return
     */
    public Patient findFirstBySickbedIdAndIsLeave(Long sickbedId,Boolean isLeave);

    /**
     * 根据病历号查询
     * @param medicalRecordNo
     * @return
     */
    public Patient findFirstByMedicalRecordNo(String medicalRecordNo);
}
