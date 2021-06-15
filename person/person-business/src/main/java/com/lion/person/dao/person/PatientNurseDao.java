package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.PatientNurse;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午9:01
 */
public interface PatientNurseDao extends BaseDao<PatientNurse> {

    /**
     * 根据患者删除
     * @param patientId
     * @return
     */
    public int deleteByPatientId(Long patientId);

    /**
     * 根据患者查询
     * @param patientId
     * @return
     */
    public List<PatientNurse> findByPatientId(Long patientId);
}
