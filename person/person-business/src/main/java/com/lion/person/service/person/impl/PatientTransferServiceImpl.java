package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.dao.person.PatientTransferDao;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.dto.ReceivePatientDto;
import com.lion.person.entity.person.dto.TransferDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.dto.UpdateTransferDto;
import com.lion.person.entity.person.vo.ListPatientTransferVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

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
            BusinessException.throwException(MessageI18nUtil.getMessage("1000041"));
        }
        if (Objects.isNull(department)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000042"));
        }
        if (Objects.isNull(patient)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000043"));
        }
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
        patientTransfer.setState(TransferState.PENDING_TRANSFER);
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
            UpdatePatientDto updatePatientDto = new ReceivePatientDto();
            BeanUtils.copyProperties(receivePatientDto, updatePatientDto);
            patientService.update(updatePatientDto);
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
        List<PatientTransfer> list = patientTransferDao.findByPatientIdOrderByCreateDateTimeDesc(patientId);
        List<ListPatientTransferVo> returnList = new ArrayList<>();
        list.forEach(patientTransfer -> {
            ListPatientTransferVo vo = new ListPatientTransferVo();
            BeanUtils.copyProperties(patientTransfer,vo);
            Department newDepartment = departmentExposeService.findById(vo.getNewDepartmentId());
            vo.setNewDepartmentName(Objects.isNull(newDepartment)?"":newDepartment.getName());
            WardRoomSickbed newWardRoomSickbed = wardRoomSickbedExposeService.findById(patientTransfer.getNewSickbedId());
            vo.setNewSickbedCode(Objects.isNull(newWardRoomSickbed)?"":newWardRoomSickbed.getBedCode());
            Department oldDepartment = departmentExposeService.findById(vo.getOldDepartmentId());
            vo.setOldDepartmentName(Objects.isNull(oldDepartment)?"":oldDepartment.getName());
            WardRoomSickbed oldWardRoomSickbed = wardRoomSickbedExposeService.findById(patientTransfer.getOldSickbedId());
            vo.setOldSickbedCode(Objects.isNull(oldWardRoomSickbed)?"":oldWardRoomSickbed.getBedCode());
            User ransferUser = userExposeService.findById(vo.getCreateUserId());
            vo.setRansferUserName(Objects.isNull(ransferUser)?"":ransferUser.getName());
            vo.setRansferUserHeadPortrait(Objects.isNull(ransferUser)?null:ransferUser.getHeadPortrait());
            vo.setRansferUserHeadPortraitUrl(fileExposeService.getUrl(Objects.isNull(ransferUser)?null:ransferUser.getHeadPortrait()));
            User receiveUser = userExposeService.findById(vo.getReceiveUserId());
            vo.setReceiveUserName(Objects.isNull(receiveUser)?"":receiveUser.getName());
            vo.setReceiveUserHeadPortrait(Objects.isNull(receiveUser)?null:receiveUser.getHeadPortrait());
            vo.setReceiveUserHeadPortraitUrl(fileExposeService.getUrl(Objects.isNull(receiveUser)?null:receiveUser.getHeadPortrait()));
            vo.setPatientDetailsVo(patientService.details(patientTransfer.getPatientId()));
            returnList.add(vo);
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
            patientTransfer.setState(updateTransferDto.getTransferState());
            update(patientTransfer);
        }
    }

}
