package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.PatientTransfer;

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
}
