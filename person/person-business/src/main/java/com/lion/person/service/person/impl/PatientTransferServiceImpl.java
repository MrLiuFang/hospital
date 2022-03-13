package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.dao.person.PatientTransferDao;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.dto.ReceivePatientDto;
import com.lion.person.entity.person.dto.TransferDto;
import com.lion.person.entity.person.dto.UpdateTransferDto;
import com.lion.person.entity.person.vo.ListPatientTransferVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    public void transfer(TransferDto transferDto) {
        PatientTransfer patientTransfer = new PatientTransfer();
        com.lion.core.Optional<Patient> optionalPatient = patientService.findById(transferDto.getPatientId());
        com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(transferDto.getDepartmentId());
        List<TransferState> state = new ArrayList<>();
        state.add(TransferState.CANCEL);
        state.add(TransferState.FINISH);
        PatientTransfer oldPatientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(transferDto.getPatientId(),state);
        if (Objects.nonNull(oldPatientTransfer)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000041"));
        }
        if (optionalDepartment.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000042"));
        }
        if (optionalPatient.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000043"));
        }
        Patient patient = optionalPatient.get();
        if (Objects.equals(patient.getIsLeave(),true)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000044"));
        }
        if (Objects.equals(transferDto.getDepartmentId(),patient.getDepartmentId())) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000045"));
        }
        Long userId = CurrentUserUtil.getCurrentUserId();
        patientTransfer.setNewDepartmentId(transferDto.getDepartmentId());
        patientTransfer.setPatientId(transferDto.getPatientId());
        patientTransfer.setOldSickbedId(patient.getSickbedId());
        patientTransfer.setOldDepartmentId(patient.getDepartmentId());
        patientTransfer.setState(TransferState.TRANSFERRING);
        save(patientTransfer);

    }

    @Override
    @Transactional
    public void receiveOrCancel(ReceivePatientDto receivePatientDto) {
        if (Objects.isNull(receivePatientDto.getId())) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000026"));
        }
        if (!(Objects.equals(receivePatientDto.getState(),TransferState.FINISH) || Objects.equals(receivePatientDto.getState(),TransferState.CANCEL))) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000046"));
        }
        if (Objects.equals(receivePatientDto.getState(),TransferState.FINISH)) {
            Patient patient = new Patient();
            BeanUtils.copyProperties(receivePatientDto, patient);
            patientService.update(patient);
        }
        List<TransferState> state = new ArrayList<>();
        state.add(TransferState.CANCEL);
        state.add(TransferState.FINISH);
        PatientTransfer patientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(receivePatientDto.getId(),state);
        patientTransfer.setState(receivePatientDto.getState());
        if (Objects.equals(receivePatientDto.getState(),TransferState.FINISH)) {
            patientTransfer.setNewSickbedId(receivePatientDto.getSickbedId());
        }
        Long userId = CurrentUserUtil.getCurrentUserId();
        patientTransfer.setReceiveUserId(userId);
        patientTransfer.setReceiveDateTime(LocalDateTime.now());
        update(patientTransfer);
    }

    @Override
    public List<ListPatientTransferVo> list(Long patientId) {
        List<PatientTransfer> list = Collections.EMPTY_LIST;
        if (Objects.isNull(patientId)){
            list = this.findAll(Sort.by(Sort.Direction.DESC,"createDateTime"));
        }else {
            list = patientTransferDao.findByPatientIdAndStateOrderByCreateDateTimeDesc(patientId,TransferState.FINISH );
        }

        List<ListPatientTransferVo> returnList = new ArrayList<>();
        list.forEach(patientTransfer -> {
            ListPatientTransferVo vo = new ListPatientTransferVo();
            BeanUtils.copyProperties(patientTransfer,vo);
            PatientDetailsVo patientDetailsVo = patientService.details(patientTransfer.getPatientId());
            if (Objects.nonNull(patientDetailsVo)) {
                vo.setPatientDetailsVo(patientDetailsVo);
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(vo.getNewDepartmentId());
                vo.setNewDepartmentName(optionalDepartment.isEmpty() ? "" : optionalDepartment.get().getName());
                com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(patientTransfer.getNewSickbedId());
                vo.setNewSickbedCode(optionalWardRoomSickbed.isEmpty() ? "" : optionalWardRoomSickbed.get().getBedCode());
                com.lion.core.Optional<Department> optionalDepartment1 = departmentExposeService.findById(vo.getOldDepartmentId());
                vo.setOldDepartmentName(optionalDepartment1.isEmpty() ? "" : optionalDepartment1.get().getName());
                com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed1 = wardRoomSickbedExposeService.findById(patientTransfer.getOldSickbedId());
                vo.setOldSickbedCode(optionalWardRoomSickbed1.isEmpty() ? "" : optionalWardRoomSickbed1.get().getBedCode());
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(vo.getCreateUserId());
                vo.setRansferUserName(optionalUser.isEmpty() ? "" : optionalUser.get().getName());
                vo.setRansferUserHeadPortrait(optionalUser.isEmpty() ? null : optionalUser.get().getHeadPortrait());
                vo.setRansferUserHeadPortraitUrl(fileExposeService.getUrl(optionalUser.isEmpty() ? null : optionalUser.get().getHeadPortrait()));
                com.lion.core.Optional<User> optionalUser1 = userExposeService.findById(vo.getReceiveUserId());
                vo.setReceiveUserName(optionalUser1.isEmpty() ? "" : optionalUser1.get().getName());
                vo.setReceiveUserHeadPortrait(optionalUser1.isEmpty() ? null : optionalUser1.get().getHeadPortrait());
                vo.setReceiveUserHeadPortraitUrl(fileExposeService.getUrl(optionalUser1.isEmpty() ? null : optionalUser1.get().getHeadPortrait()));
                returnList.add(vo);
            }
        });
        return returnList;
    }

    @Override
    public void updateState(UpdateTransferDto updateTransferDto) {
        if (!Objects.equals(updateTransferDto.getTransferState(),TransferState.ROUTINE)) {
            List<TransferState> list = new ArrayList<>();
            list.add(TransferState.FINISH);
            list.add(TransferState.CANCEL);
            PatientTransfer patientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(updateTransferDto.getPatientId(),list);
            if (Objects.nonNull(patientTransfer)) {
                patientTransfer.setState(updateTransferDto.getTransferState());
                update(patientTransfer);
            }
        }
    }

}
