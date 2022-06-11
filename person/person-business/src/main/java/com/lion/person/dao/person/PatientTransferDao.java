package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.PatientTransfer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午5:32
 */
public interface PatientTransferDao extends BaseDao<PatientTransfer> {

    /**
     * 查询未转移完成/取消的患者
     * @param patientId
     * @param state
     * @return
     */
    public PatientTransfer findFirstByPatientIdAndStateNotIn(Long patientId, List<TransferState> state);

    /**
     *
     * @param patientId
     * @param state
     * @return
     */
    public PatientTransfer findFirstByPatientIdAndState(Long patientId, TransferState state);

    /**
     *
     * @param state
     * @return
     */
    public List<PatientTransfer> findByState(TransferState state);

    /**
     * 根据患者查询
     * @param patientId
     * @param state
     * @return
     */
    public List<PatientTransfer> findByPatientIdAndStateOrderByCreateDateTimeDesc(Long patientId,TransferState state);

    /**
     * 根据患者删除转移记录
     * @param patientId
     * @return
     */
    public int deleteByPatientId(Long patientId);

    @Transactional
    @Modifying
    @Query(" update PatientTransfer set state =:state where patientId=:patientId")
    public int update1(Long patientId, TransferState state);

    @Transactional
    @Modifying
    @Query(" update PatientTransfer set state =:state , newSickbedId=:newSickbedId where patientId=:patientId")
    public int update2(Long patientId, TransferState state,Long newSickbedId);


}
