package com.lion.person.service.person.impl;

import com.lion.common.constants.RedisConstants;
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
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.ward.WardExposeService;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.dao.person.PatientTransferDao;
import com.lion.person.dao.person.TempLeaveDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.PatientLeaveDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.RestrictedAreaService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private PatientTransferDao patientTransferDao;

    @Autowired
    private TempLeaveDao tempLeaveDao;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Autowired
    private PatientLogService patientLogService;

    private RedisTemplate redisTemplate;

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
        patientLogService.add("添加患者",patient.getId());
        redisTemplate.opsForValue().set(RedisConstants.PATIENT+patient.getId(),patient,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void update(UpdatePatientDto updatePatientDto) {
        Patient oldPatient = findById(updatePatientDto.getId());
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
        if (Objects.nonNull(patient.getBindPatientId()) && !Objects.equals(oldPatient.getBindPatientId(),patient.getBindPatientId())) {
            patientLogService.add("修改绑定患者",patient.getId());
        }
        if (Objects.nonNull(patient.getDoctorId()) && !Objects.equals(oldPatient.getDoctorId(),patient.getDoctorId())) {
            patientLogService.add("修改负责医生",patient.getId());
        }
        if (Objects.nonNull(patient.getTagCode()) && !Objects.equals(oldPatient.getTagCode(),patient.getTagCode())) {
            patientLogService.add("修改标签码",patient.getId());
        }
        if (Objects.nonNull(patient.getDepartmentId()) && !Objects.equals(oldPatient.getDepartmentId(),patient.getDepartmentId())) {
            patientLogService.add("修改科室",patient.getId());
        }
        if (Objects.nonNull(patient.getNurseId()) && !Objects.equals(oldPatient.getNurseId(),patient.getNurseId())) {
            patientLogService.add("修改负责护士",patient.getId());
        }
        if (Objects.nonNull(patient.getSickbedId()) && !Objects.equals(oldPatient.getSickbedId(),patient.getSickbedId())) {
            patientLogService.add("修改床位",patient.getId());
        }
        if (Objects.nonNull(patient.getBirthday()) && !Objects.equals(oldPatient.getBirthday(),patient.getBirthday())) {
            patientLogService.add("修改出生日期",patient.getId());
        }
        if (Objects.nonNull(patient.getMedicalRecordNo()) && !Objects.equals(oldPatient.getMedicalRecordNo(),patient.getMedicalRecordNo())) {
            patientLogService.add("修改病历号",patient.getId());
        }
        if (Objects.nonNull(patient.getDisease()) && !Objects.equals(oldPatient.getDisease(),patient.getDisease())) {
            patientLogService.add("修改疾病",patient.getId());
        }
        if (Objects.nonNull(patient.getRemarks()) && !Objects.equals(oldPatient.getRemarks(),patient.getRemarks())) {
            patientLogService.add("修改备注",patient.getId());
        }
        if (Objects.nonNull(patient.getAddress()) && !Objects.equals(oldPatient.getAddress(),patient.getAddress())) {
            patientLogService.add("修改地址",patient.getId());
        }
        redisTemplate.opsForValue().set(RedisConstants.PATIENT+patient.getId(),patient,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
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
                redisTemplate.delete(RedisConstants.PATIENT+deleteDto.getId());
            });
        }
    }

    @Override
    public IPageResultData<List<ListPatientVo>> list(String name, Boolean isLeave, LocalDateTime birthday, TransferState transferState, Boolean isNormal, String tagCode, String medicalRecordNo, Long sickbedId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> departmentList = departmentResponsibleUserExposeService.findDepartment(userId);
        List<Long> departmentIds = new ArrayList<>();
        departmentIds.add(Long.MAX_VALUE);
        departmentList.forEach(department -> {
            departmentIds.add(department.getId());
        });
        jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        if (Objects.nonNull(transferState)) {
            List<PatientTransfer> list =patientTransferDao.findByState(transferState);
            if (Objects.nonNull(list) && list.size()>0) {
                List<Long> ids = new ArrayList<>();
                list.forEach(patientTransfer -> {
                    ids.add(patientTransfer.getPatientId());
                });
                if (ids.size()>0) {
                    jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",ids);
                }
            }
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(isLeave)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isLeave",isLeave);
        }
        if (Objects.nonNull(birthday)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_birthday",birthday);
        }
        if (Objects.nonNull(isNormal)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isNormal",isNormal);
        }
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagCode",tagCode);
        }
        if (StringUtils.hasText(medicalRecordNo)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_medicalRecordNo",medicalRecordNo);
        }
        if (Objects.nonNull(sickbedId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_sickbedId",sickbedId);
        }
        if (Objects.nonNull(startDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",endDateTime);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Patient> page = this.findNavigator(lionPage);
        List<Patient> list = page.getContent();
        List<ListPatientVo> returnList = new ArrayList<>();
        list.forEach(patient -> {
            ListPatientVo vo = new ListPatientVo();
            PatientDetailsVo patientDetailsVo = details(patient.getId());
            if (Objects.nonNull(patientDetailsVo)) {
                BeanUtils.copyProperties(patientDetailsVo, vo);
                PatientTransfer  patientTransfer = patientTransferDao.findFirstByPatientIdAndState(patient.getId(),transferState);
                if (Objects.nonNull(patientTransfer)){
                    vo.setLeaveDateTime(patientTransfer.getLeaveDateTime());
                    vo.setTriggerDateTime(vo.getTriggerDateTime());
                }
                returnList.add(vo);
            }
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
        if (Objects.nonNull(patient.getBirthday())) {
            Period period = Period.between(patient.getBirthday(), LocalDate.now());
            vo.setAge(period.getYears());
        }
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
        List<RestrictedArea> restrictedAreaList = restrictedAreaService.find(patient.getId(), PersonType.PATIENT);
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
        List<TransferState> state = new ArrayList<>();
        state.add(TransferState.CANCEL);
        state.add(TransferState.FINISH);
        PatientTransfer  patientTransfer = patientTransferDao.findFirstByPatientIdAndStateNotIn(patient.getId(),state);
        if (Objects.nonNull(patientTransfer)){
            vo.setNewDepartmentId(patientTransfer.getNewDepartmentId());
            Department department = departmentExposeService.findById(patientTransfer.getNewDepartmentId());
            if (Objects.nonNull(department)){
                vo.setNewDepartmentName(department.getName());
            }
        }
        TempLeave tempLeave = tempLeaveDao.findFirstByPatientIdOrderByCreateDateTimeDesc(patient.getId());
        if (Objects.nonNull(tempLeave)){
            PatientDetailsVo.TempLeaveVo tempLeaveVo = new PatientDetailsVo.TempLeaveVo();
            tempLeaveVo.setUserId(tempLeave.getUserId());
            User user = userExposeService.findById(tempLeave.getUserId());
            if (Objects.nonNull(user)){
                tempLeaveVo.setUserName(user.getName());
                tempLeaveVo.setHeadPortrait(user.getHeadPortrait());
                tempLeaveVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                tempLeaveVo.setStartDateTime(tempLeave.getStartDateTime());
                tempLeaveVo.setEndDateTime(tempLeave.getEndDateTime());
            }
            vo.setTempLeaveVo(tempLeaveVo);
        }

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
        tagPatientExposeService.unbinding(patient.getId(),false);
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
