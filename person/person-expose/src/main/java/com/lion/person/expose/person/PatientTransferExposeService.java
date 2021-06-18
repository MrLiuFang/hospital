package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.PatientTransfer;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 下午12:00
 */
public interface PatientTransferExposeService extends BaseService<PatientTransfer> {

    /**
     * 查询患者转移
     * @param patientId
     * @return
     */
    public PatientTransfer find(Long patientId);

    /**
     * 修改转移状态
     * @param patientId
     * @param state
     */
    public void updateState(Long patientId, TransferState state);
}
