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
import com.lion.person.entity.person.vo.ListPatientTransferVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Long userId = CurrentUserUtil.getCurrentUserId();
        patientTransfer.setNewDepartmentId(transferDto.getDepartmentId());
        patientTransfer.setPatientId(transferDto.getPatientId());
        patientTransfer.setOldSickbedId(patient.getSickbedId());
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
        patientTransfer.setNewSickbedId(receivePatientDto.getNewSickbedId());
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
}
