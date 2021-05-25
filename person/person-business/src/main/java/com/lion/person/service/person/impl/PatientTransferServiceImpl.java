package com.lion.person.service.person.impl;

import com.lion.core.IResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.dao.person.PatientTransferDao;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.dto.ReceivePatientDto;
import com.lion.person.entity.person.dto.TransferDto;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午5:33
 */
@Service
public class PatientTransferServiceImpl extends BaseServiceImpl<PatientTransfer> implements PatientTransferService {

    @Autowired
    private PatientTransferDao patientTransferDao;

    @Autowired
    private PatientService patientService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Override
    public void transfer(TransferDto transferDto) {
        PatientTransfer patientTransfer = new PatientTransfer();
        Patient patient = patientService.findById(transferDto.getPatientId());
        Department department = departmentExposeService.findById(transferDto.getDepartmentId());
        List<TransferState> state = new ArrayList<>();
        state.add(TransferState.CANCEL);
        state.add(TransferState.FINISH);
        PatientTransfer oldPatientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(transferDto.getPatientId(),state);
        if (Objects.nonNull(oldPatientTransfer)) {
            BusinessException.throwException("该患者有未完成/取消的转移操作");
        }
        if (Objects.isNull(department)) {
            BusinessException.throwException("转移的科室不存在");
        }
        if (Objects.isNull(patient)) {
            BusinessException.throwException("该患者不存在");
        }
        if (Objects.equals(patient.getIsLeave(),true)){
            BusinessException.throwException("该患者已登出");
        }
        if (Objects.equals(transferDto.getDepartmentId(),patient.getDepartmentId())) {
            BusinessException.throwException("不能从当前科室转移至当前科室");
        }
        patientTransfer.setNewDepartmentId(transferDto.getDepartmentId());
        patientTransfer.setPatientId(transferDto.getPatientId());
        patientTransfer.setOldDepartmentId(patient.getDepartmentId());
        patientTransfer.setState(TransferState.PENDING_TRANSFER);
        save(patientTransfer);
    }

    @Override
    public void receiveOrCancel(ReceivePatientDto receivePatientDto) {
        if (!(Objects.equals(receivePatientDto.getState(),TransferState.FINISH) || Objects.equals(receivePatientDto.getState(),TransferState.CANCEL))) {
            BusinessException.throwException("只能进行接受/取消转移操作");
        }
        List<TransferState> state = new ArrayList<>();
        state.add(TransferState.CANCEL);
        state.add(TransferState.FINISH);
        PatientTransfer patientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(receivePatientDto.getPatientId(),state);
        patientTransfer.setState(receivePatientDto.getState());
        update(patientTransfer);
    }
}
