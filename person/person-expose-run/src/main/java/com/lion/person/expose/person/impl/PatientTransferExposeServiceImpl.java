package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientTransferDao;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.expose.person.PatientTransferExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 下午12:01
 */
@DubboService
public class PatientTransferExposeServiceImpl extends BaseServiceImpl<PatientTransfer> implements PatientTransferExposeService {

    @Autowired
    private PatientTransferDao patientTransferDao;

    @Override
    public PatientTransfer find(Long patientId) {
        return patientTransferDao.findFirstByPatientIdAndState(patientId, TransferState.PENDING_TRANSFER);
    }

    @Override
    public void updateState(Long patientId, TransferState state) {
        com.lion.core.Optional<PatientTransfer> optional = findById(patientId);
        if (optional.isPresent()){
            PatientTransfer patientTransfer = optional.get();
            patientTransfer.setState(state);
            if (Objects.equals(TransferState.TRANSFERRING,state)) {
                patientTransfer.setLeaveDateTime(LocalDateTime.now());
                patientTransfer.setTriggerDateTime(LocalDateTime.now());
            }
            update(patientTransfer);
        }
    }
}
