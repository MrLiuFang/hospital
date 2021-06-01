package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Modifying
    @Transactional
    @Query(" update Patient  set deviceSate =:state where id = :id ")
    public void updateState(@Param("id")Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update Patient  set lastDataTime =:dateTime where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);
}
