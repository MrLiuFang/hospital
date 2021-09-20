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
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.SystemAlarmType;
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
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.entity.person.PatientNurse;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.PatientLeaveDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientDoctorService;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientNurseService;
import com.lion.person.service.person.PatientService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

//    @Autowired
//    private RestrictedAreaService restrictedAreaService;

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

//    @Autowired
//    private TempLeaveDao tempLeaveDao;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Autowired
    private PatientLogService patientLogService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PatientDoctorService patientDoctorService;

    @Autowired
    private PatientNurseService patientNurseService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @DubboReference
    private SystemAlarmExposeService systemAlarmExposeService;

    @Override
    @Transactional
//    @GlobalTransactional
    public void add(AddPatientDto addPatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(addPatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),null);
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),null);
        patient = setOtherInfo(patient);
        patient = save(patient);
        patientNurseService.add(addPatientDto.getNurseIds(),patient.getId());
        patientDoctorService.add(addPatientDto.getDoctorIds(),patient.getId());
//        restrictedAreaService.add(addPatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
        patientLogService.add("", LogType.ADD, CurrentUserUtil.getCurrentUserId(), patient.getId());
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
        update(patient);
        patientNurseService.add(updatePatientDto.getNurseIds(),patient.getId());
        patientDoctorService.add(updatePatientDto.getDoctorIds(),patient.getId());
//        restrictedAreaService.add(updatePatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
        if (Objects.nonNull(patient.getBindPatientId()) && !Objects.equals(oldPatient.getBindPatientId(),patient.getBindPatientId())) {
            patientLogService.add("",LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getTagCode()) && !Objects.equals(oldPatient.getTagCode(),patient.getTagCode())) {
            patientLogService.add(patient.getTagCode(), LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getDepartmentId()) && !Objects.equals(oldPatient.getDepartmentId(),patient.getDepartmentId())) {
            Department department = departmentExposeService.findById(patient.getDepartmentId());
            patientLogService.add(Objects.nonNull(department)?department.getName():"",LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getSickbedId()) && !Objects.equals(oldPatient.getSickbedId(),patient.getSickbedId())) {
            WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
            patientLogService.add(Objects.nonNull(wardRoomSickbed)?wardRoomSickbed.getBedCode():"",LogType.UPDATE_WARD, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getBirthday()) && !Objects.equals(oldPatient.getBirthday(),patient.getBirthday())) {
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            patientLogService.add(Objects.nonNull(patient.getBirthday())?dtf2.format(patient.getBirthday()):"",LogType.UPDATE_BIRTHDAY, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getMedicalRecordNo()) && !Objects.equals(oldPatient.getMedicalRecordNo(),patient.getMedicalRecordNo())) {
            patientLogService.add(patient.getMedicalRecordNo(),LogType.UPDATE_MEDICAL_RECORD_NO, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getDisease()) && !Objects.equals(oldPatient.getDisease(),patient.getDisease())) {
            patientLogService.add(patient.getDisease(), LogType.UPDATE_DISEASE, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getRemarks()) && !Objects.equals(oldPatient.getRemarks(),patient.getRemarks())) {
            patientLogService.add(patient.getRemarks(), LogType.UPDATE_REMARKS, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getAddress()) && !Objects.equals(oldPatient.getAddress(),patient.getAddress())) {
            patientLogService.add(patient.getAddress(), LogType.UPDATE_ADDRESS, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getLevel()) && !Objects.equals(oldPatient.getLevel(),patient.getLevel())) {
            patientLogService.add(String.valueOf(patient.getLevel()), LogType.UPDATE_LEVEL, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getActionMode()) && !Objects.equals(oldPatient.getActionMode(),patient.getActionMode())) {
            patientLogService.add(patient.getActionMode().getName(), LogType.UPDATE_ACTION_MODE, userId, patient.getId());
        }
        if (Objects.nonNull(patient.getTimeQuantum()) && !Objects.equals(oldPatient.getTimeQuantum(),patient.getTimeQuantum())) {
            patientLogService.add(patient.getTimeQuantum(), LogType.UPDATE_TIME_QUANTUM, userId, patient.getId());
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
//                restrictedAreaService.delete(deleteDto.getId());
                tagPatientExposeService.unbinding(deleteDto.getId(),true);
                redisTemplate.delete(RedisConstants.PATIENT+deleteDto.getId());
                currentPositionExposeService.delete(deleteDto.getId(),null,null);
            });
        }
    }

    @Override
    public IPageResultData<List<ListPatientVo>> list(String name, Boolean isLeave, Boolean isWaitLeave, LocalDateTime birthday, TransferState transferState, String tagCode, String medicalRecordNo, Long sickbedId, LocalDateTime startDateTime, LocalDateTime endDateTime, String cardNumber, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        if (Objects.nonNull(transferState) && !Objects.equals(transferState,TransferState.ROUTINE)) {
            List<PatientTransfer> list =patientTransferDao.findByState(transferState);
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            if (Objects.nonNull(list) && list.size()>0) {
                list.forEach(patientTransfer -> {
                    ids.add(patientTransfer.getPatientId());
                });
            }
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",ids);
            }
        }else if (Objects.nonNull(transferState) && Objects.equals(transferState,TransferState.ROUTINE)) {
            List<PatientTransfer> list =patientTransferDao.findByState(TransferState.TRANSFERRING);
            List<PatientTransfer> list1 =patientTransferDao.findByState(TransferState.PENDING_TRANSFER);
            List<PatientTransfer> list2 =patientTransferDao.findByState(TransferState.WAITING_TO_RECEIVE);
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            list.forEach(patientTransfer -> {
                ids.add(patientTransfer.getPatientId());
            });
            list1.forEach(patientTransfer -> {
                ids.add(patientTransfer.getPatientId());
            });
            list2.forEach(patientTransfer -> {
                ids.add(patientTransfer.getPatientId());
            });
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.NOT_IN+"_id",ids);
            }
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(isLeave)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isLeave",isLeave);
        }
        if (Objects.nonNull(isWaitLeave)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isWaitLeave",isWaitLeave);
        }
        if (Objects.nonNull(birthday)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_birthday",birthday);
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
        if (StringUtils.hasText(cardNumber)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_cardNumber",cardNumber);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
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
                    vo.setTriggerDateTime(patientTransfer.getTriggerDateTime());
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
        List<PatientNurse> patientNurses = patientNurseService.find(patient.getId());
        List<PatientDetailsVo.NurseVo> nurseVos = new ArrayList<>();
        patientNurses.forEach(patientNurse -> {
            User nurse = userExposeService.findById(patientNurse.getNurseId());
            PatientDetailsVo.NurseVo nurseVo = new PatientDetailsVo.NurseVo();
            if (Objects.nonNull(nurse)){
                nurseVo.setNurseId(nurse.getId());
                nurseVo.setNurseName(nurse.getName());
                nurseVo.setNurseHeadPortrait(nurse.getHeadPortrait());
                nurseVo.setNurseHeadPortraitUrl(fileExposeService.getUrl(nurse.getHeadPortrait()));
                nurseVos.add(nurseVo);
            }
        });
        vo.setNurseVos(nurseVos);
        List<PatientDoctor> patientDoctors = patientDoctorService.find(patient.getId());
        List<PatientDetailsVo.DoctorVo> doctorVos = new ArrayList<>();
        patientDoctors.forEach(patientDoctor -> {
            User doctor = userExposeService.findById(patientDoctor.getDoctorId());
            PatientDetailsVo.DoctorVo doctorVo = new PatientDetailsVo.DoctorVo();
            if (Objects.nonNull(doctor)){
                doctorVo.setDoctorId(doctor.getId());
                doctorVo.setDoctorName(doctor.getName());
                doctorVo.setDoctorHeadPortrait(doctor.getHeadPortrait());
                doctorVo.setDoctorHeadPortraitUrl(fileExposeService.getUrl(doctor.getHeadPortrait()));
                doctorVos.add(doctorVo);
            }
        });
        vo.setDoctorVos(doctorVos);

//        List<RestrictedArea> restrictedAreaList = restrictedAreaService.find(patient.getId(), PersonType.PATIENT);
//        List<PatientDetailsVo.RestrictedAreaVo> restrictedAreaVoList = new ArrayList<>();
//        restrictedAreaList.forEach(restrictedArea -> {
//            PatientDetailsVo.RestrictedAreaVo restrictedAreaVo = new PatientDetailsVo.RestrictedAreaVo();
//            Region region = regionExposeService.findById(restrictedArea.getRegionId());
//            if (Objects.nonNull(region)){
//                restrictedAreaVo.setRegionName(region.getName());
//                restrictedAreaVo.setRegionId(region.getId());
//                restrictedAreaVo.setRemark(region.getRemarks());
//                Build build = buildExposeService.findById(region.getBuildId());
//                if (Objects.nonNull(build)){
//                    restrictedAreaVo.setBuildName(build.getName());
//                }
//                BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
//                if (Objects.nonNull(buildFloor)) {
//                    restrictedAreaVo.setBuildFloorName(buildFloor.getName());
//                }
//                restrictedAreaVoList.add(restrictedAreaVo);
//            }
//        });
//        vo.setRestrictedAreaVoList(restrictedAreaVoList);
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
//        TempLeave tempLeave = tempLeaveDao.findFirstByPatientIdOrderByCreateDateTimeDesc(patient.getId());
//        if (Objects.nonNull(tempLeave)){
//            PatientDetailsVo.TempLeaveVo tempLeaveVo = new PatientDetailsVo.TempLeaveVo();
//            tempLeaveVo.setUserId(tempLeave.getUserId());
//            User user = userExposeService.findById(tempLeave.getUserId());
//            if (Objects.nonNull(user)){
//                tempLeaveVo.setUserName(user.getName());
//                tempLeaveVo.setHeadPortrait(user.getHeadPortrait());
//                tempLeaveVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
//                tempLeaveVo.setStartDateTime(tempLeave.getStartDateTime());
//                tempLeaveVo.setEndDateTime(tempLeave.getEndDateTime());
//            }
//            vo.setTempLeaveVo(tempLeaveVo);
//        }

        SystemAlarm systemAlarm =  systemAlarmExposeService.findLastByPi(patient.getId());
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            vo.setAlarm(systemAlarmType.getDesc());
            vo.setAlarmType(systemAlarmType.getName());
            vo.setAlarmDataTime(systemAlarm.getDt());
            vo.setAlarmId(systemAlarm.get_id());
        }

        if (Objects.nonNull(patient.getBindPatientId())){
            vo.setBindPatient(details(patient.getBindPatientId()));
        }
        return vo;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void leave(PatientLeaveDto patientLeaveDto) {
        Patient patient = findById(patientLeaveDto.getPatientId());
        patient.setId(patientLeaveDto.getPatientId());
        patient.setIsLeave(patientLeaveDto.getIsLeave());
        patient.setIsWaitLeave(false);
        patient.setLeaveRemarks(patientLeaveDto.getLeaveRemarks());
        update(patient);
        if (Objects.equals(patientLeaveDto.getIsLeave(),true)) {
            tagPatientExposeService.unbinding(patient.getId(), false);
        }
        currentPositionExposeService.delete(patient.getId(),null,null);
    }

    private Patient setOtherInfo(Patient patient) {
        WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000035"));
        }
        WardRoom wardRoom = wardRoomExposeService.findById(wardRoomSickbed.getWardRoomId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000036"));
        }
        Ward ward = wardExposeService.findById(wardRoom.getWardId());
        if (Objects.isNull(wardRoomSickbed)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000037"));
        }
        patient.setWardId(ward.getId());
        patient.setRoomId(wardRoom.getId());
        if (!Objects.equals(ward.getDepartmentId(),patient.getDepartmentId())) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000038"));
        }
        return patient;
    }


    private void sickbedIsCanUse(Long sickbedId,Long patientId) {
        Patient patient = patientDao.findFirstBySickbedIdAndIsLeave(sickbedId,false);
        if ((Objects.isNull(patientId) && Objects.nonNull(patient)) || (Objects.nonNull(patientId) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),patientId)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000039"));
        }
    }

    private void assertMedicalRecordNoExist(String medicalRecordNo, Long id) {
        Patient patient = patientDao.findFirstByMedicalRecordNo(medicalRecordNo);
        if ((Objects.isNull(id) && Objects.nonNull(patient)) || (Objects.nonNull(id) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000040"));
        }
    }

}
