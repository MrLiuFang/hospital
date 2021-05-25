package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.ward.WardExposeService;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.PatientLeaveDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.RestrictedAreaService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:07
 */
@Service
public class PatientServiceImpl extends BaseServiceImpl<Patient> implements PatientService {

    @DubboReference
    private TagPatientExposeService tagPatientExposeService;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private RestrictedAreaService restrictedAreaService;

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @DubboReference
    private WardRoomExposeService wardRoomExposeService;

    @DubboReference
    private WardExposeService wardExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Override
    @Transactional
//    @GlobalTransactional
    public void add(AddPatientDto addPatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(addPatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),null);
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),null);
        patient = setOtherInfo(patient);
        assertNurseExist(patient.getNurseId());
        assertDoctorExist(patient.getDoctorId());
        patient = save(patient);
        restrictedAreaService.add(addPatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void update(UpdatePatientDto updatePatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(updatePatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),patient.getId());
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),patient.getId());
        patient = setOtherInfo(patient);
        assertNurseExist(patient.getNurseId());
        assertDoctorExist(patient.getDoctorId());
        update(patient);
        restrictedAreaService.add(updatePatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void delete(List<DeleteDto> deleteDtos) {
        if (Objects.nonNull(deleteDtos) ){
            deleteDtos.forEach(deleteDto -> {
                this.deleteById(deleteDto.getId());
                restrictedAreaService.delete(deleteDto.getId());
                tagPatientExposeService.unbinding(deleteDto.getId(),true);
            });
        }
    }

    @Override
    public IPageResultData<List<ListPatientVo>> list(String name, Boolean isLeave, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(isLeave)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isLeave",isLeave);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Patient> page = this.findNavigator(lionPage);
        List<Patient> list = page.getContent();
        List<ListPatientVo> returnList = new ArrayList<>();
        list.forEach(patient -> {
            ListPatientVo vo = new ListPatientVo();
            PatientDetailsVo patientDetailsVo = details(patient.getId());
            BeanUtils.copyProperties(patientDetailsVo,vo);
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public PatientDetailsVo details(Long id) {
        Patient patient = this.findById(id);
        if (Objects.isNull(patient)){
            return null;
        }
        PatientDetailsVo vo = new PatientDetailsVo();
        BeanUtils.copyProperties(patient,vo);
        if (Objects.nonNull(vo.getSickbedId())) {
            WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(vo.getSickbedId());
            if (Objects.nonNull(wardRoomSickbed)) {
                vo.setBedCode(wardRoomSickbed.getBedCode());
            }
        }
        if (Objects.nonNull(vo.getDepartmentId())) {
            Department department = departmentExposeService.findById(vo.getDepartmentId());
            vo.setDepartmentName(department.getName());
        }
        vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
        if (Objects.nonNull(patient.getNurseId())){
            User nurse = userExposeService.findById(patient.getNurseId());
            if (Objects.nonNull(nurse)){
                vo.setNurseName(nurse.getName());
                vo.setNurseHeadPortrait(nurse.getHeadPortrait());
                vo.setNurseHeadPortraitUrl(fileExposeService.getUrl(nurse.getHeadPortrait()));
            }
        }
        if (Objects.nonNull(patient.getDoctorId())) {
            User doctor = userExposeService.findById(patient.getDoctorId());
            if (Objects.nonNull(doctor)){
                vo.setDoctorName(doctor.getName());
                vo.setDoctorHeadPortrait(doctor.getHeadPortrait());
                vo.setDoctorHeadPortraitUrl(fileExposeService.getUrl(doctor.getHeadPortrait()));
            }
        }
        List<RestrictedArea> restrictedAreaList = restrictedAreaService.find(patient.getId());
        List<PatientDetailsVo.RestrictedAreaVo> restrictedAreaVoList = new ArrayList<>();
        restrictedAreaList.forEach(restrictedArea -> {
            PatientDetailsVo.RestrictedAreaVo restrictedAreaVo = new PatientDetailsVo.RestrictedAreaVo();
            Region region = regionExposeService.findById(restrictedArea.getRegionId());
            if (Objects.nonNull(region)){
                restrictedAreaVo.setRegionName(region.getName());
                restrictedAreaVo.setRegionId(region.getId());
                restrictedAreaVo.setRemark(region.getRemarks());
                Build build = buildExposeService.findById(region.getBuildId());
                if (Objects.nonNull(build)){
                    restrictedAreaVo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    restrictedAreaVo.setBuildFloorName(buildFloor.getName());
                }
                restrictedAreaVoList.add(restrictedAreaVo);
            }
        });
        vo.setRestrictedAreaVoList(restrictedAreaVoList);
        if (Objects.nonNull(patient.getBindPatientId())){
            vo.setBindPatient(details(patient.getBindPatientId()));
        }
        return vo;
    }

    @Override
    public void leave(PatientLeaveDto patientLeaveDto) {
        Patient patient = new Patient();
        patient.setId(patientLeaveDto.getPatientId());
        patient.setIsLeave(true);
        patient.setLeaveRemarks(patientLeaveDto.getLeaveRemarks());
        update(patient);
    }

    private Patient setOtherInfo(Patient patient) {
        WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException("该床位不存在");
        }
        WardRoom wardRoom = wardRoomExposeService.findById(wardRoomSickbed.getWardRoomId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException("该床位所属的房间不存在");
        }
        Ward ward = wardExposeService.findById(wardRoom.getWardId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException("该床位所属的病房不存在");
        }
        patient.setWardId(ward.getId());
        patient.setRoomId(wardRoom.getId());
        patient.setDepartmentId(ward.getDepartmentId());
        return patient;
    }


    private void sickbedIsCanUse(Long sickbedId,Long patientId) {
        Patient patient = patientDao.findFirstBySickbedIdAndIsLeave(sickbedId,false);
        if ((Objects.isNull(patientId) && Objects.nonNull(patient)) || (Objects.nonNull(patientId) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),patientId)) ){
            BusinessException.throwException("该床位已有患者未登出,不能使用");
        }
    }

    private void assertNurseExist(Long nurseId) {
        User user = userExposeService.findById(nurseId);
        if (Objects.isNull(user)){
            BusinessException.throwException("该护士不存在");
        }
        if (!Objects.equals(user.getUserType(), UserType.NURSE)) {
            BusinessException.throwException("选择的负责护士非护士人员");
        }
    }

    private void assertDoctorExist(Long doctorId) {
        User user = userExposeService.findById(doctorId);
        if (Objects.isNull(user)){
            BusinessException.throwException("该医生不存在");
        }
        if (!Objects.equals(user.getUserType(), UserType.DOCTOR)) {
            BusinessException.throwException("选择的负责医生非医生人员");
        }
    }

    private void assertMedicalRecordNoExist(String medicalRecordNo, Long id) {
        Patient patient = patientDao.findFirstByMedicalRecordNo(medicalRecordNo);
        if ((Objects.isNull(id) && Objects.nonNull(patient)) || (Objects.nonNull(id) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),id)) ){
            BusinessException.throwException("该病历号已被使用");
        }
    }
}
