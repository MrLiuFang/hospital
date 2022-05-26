package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.enums.PatientState;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:03
 */
public interface PatientDao extends BaseDao<Patient>, PatientDaoEx {

    /**
     * 根据床位查询患者
     * @param sickbedId
     * @param isLeave
     * @return
     */
    public Patient findFirstBySickbedIdAndIsLeave(Long sickbedId,Boolean isLeave);

    /**
     * 统计被使用的床位数量
     * @param sickbedIds
     * @param isLeave
     * @return
     */
    public int countBySickbedIdInAndIsLeave(List<Long> sickbedIds, Boolean isLeave);

    /**
     * 根据病历号查询
     * @param medicalRecordNo
     * @return
     */
    public Patient findFirstByMedicalRecordNo(String medicalRecordNo);

    @Modifying
    @Transactional
    @Query(" update Patient  set deviceState =:state  ,version=version +1 where id = :id ")
    public void updateState(@Param("id")Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update Patient  set patientState =:patientState  ,version=version +1 where id = :id ")
    public void updatePatientState(@Param("id")Long id, @Param("patientState") PatientState patientState);

    @Modifying
    @Transactional
    @Query(" update Patient  set patientState =null  ,version=version +1 where id = :id ")
    public void updatePatientStateIsNull(@Param("id")Long id);

    @Modifying
    @Transactional
    @Query(" update Patient  set isWaitLeave =:isWaitLeave ,version = version+1 where id = :id ")
    public void updateIsWaitLeave(@Param("id")Long id, @Param("isWaitLeave") Boolean isWaitLeave);

    @Modifying
    @Transactional
    @Query(" update Patient  set lastDataTime =:dateTime ,version = version+1 where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);

    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @return
     */
    public int countByDepartmentIdAndIsLeave(Long departmentId, Boolean isLeave);

    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @param ids
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndIdIn(Long departmentId, Boolean isLeave,List<Long> ids);

    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @param deviceState
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndDeviceState(Long departmentId,Boolean isLeave,State deviceState);

    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @param deviceState
     * @param ids
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndDeviceStateAndIdIn(Long departmentId,Boolean isLeave,State deviceState,List<Long> ids);

    /**
     * 根据科室查询
     * @param departmentId
     * @param isLeave
     * @param name
     * @return
     */
    public List<Patient> findByDepartmentIdAndIsLeaveAndNameLikeOrderByPatientStateDesc(Long departmentId,Boolean isLeave,String name);

    /**
     *
     * @param departmentId
     * @param isLeave
     * @param name
     * @param ids
     * @return
     */
    public List<Patient> findByDepartmentIdAndIsLeaveAndNameLikeAndIdIn(Long departmentId,Boolean isLeave,String name,List<Long> ids);

    /**
     * 根据科室查询
     * @param departmentId
     * @param isLeave
     * @return
     */
    public List<Patient> findByDepartmentIdAndIsLeaveOrderByPatientStateDesc(Long departmentId, Boolean isLeave);

    public List<Patient> findByDepartmentIdAndIsLeaveAndIdIn(Long departmentId, Boolean isLeave,List<Long> ids);

    int countByCreateDateTimeGreaterThanEqual(LocalDateTime startDateTime);

    int countByIsLeaveIsFalse();

    Optional<Patient> findFirstByCardNumberOrderByCreateDateTimeDesc(String cardNumber);

    @Query(" select p.tagCode from Patient p where p.isLeave = false ")
    public List<String> allTagCode();
}
