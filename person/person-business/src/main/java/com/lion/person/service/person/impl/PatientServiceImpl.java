package com.lion.person.service.person.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.exception.BusinessException;
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
import com.lion.person.dao.person.*;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.PatientLeaveDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.vo.*;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private TempLeaveDao tempLeaveDao;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Autowired
    private PatientLogService patientLogService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemporaryPersonDao temporaryPersonDao;

//    @Autowired
//    private PatientDoctorService patientDoctorService;
//
//    @Autowired
//    private PatientNurseService patientNurseService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @DubboReference
    private SystemAlarmExposeService systemAlarmExposeService;

    @Autowired
    private PatientReportDao patientReportDao;

    @Autowired
    private PatientTransferService patientTransferService;



    @Override
    @Transactional
//    @GlobalTransactional
    public void add(AddPatientDto addPatientDto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(addPatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),null);
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),null);
        checkSickbedHavingRegion(patient.getSickbedId());
        patient = setOtherInfo(patient);
        patient = save(patient);
//        patientNurseService.add(addPatientDto.getNurseIds(),patient.getId());
//        patientDoctorService.add(addPatientDto.getDoctorIds(),patient.getId());
//        restrictedAreaService.add(addPatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
        patientLogService.add("", LogType.ADD, CurrentUserUtil.getCurrentUserId(), patient.getId());
        redisTemplate.opsForValue().set(RedisConstants.PATIENT+patient.getId(),patient,5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void update(UpdatePatientDto updatePatientDto) {
        com.lion.core.Optional<Patient> optional = findById(updatePatientDto.getId());
        Patient patient = new Patient();
        BeanUtils.copyProperties(updatePatientDto,patient);
        sickbedIsCanUse(patient.getSickbedId(),patient.getId());
        assertMedicalRecordNoExist(patient.getMedicalRecordNo(),patient.getId());
        checkSickbedHavingRegion(patient.getSickbedId());
        patient = setOtherInfo(patient);
        update(patient);
//        patientNurseService.add(updatePatientDto.getNurseIds(),patient.getId());
//        patientDoctorService.add(updatePatientDto.getDoctorIds(),patient.getId());
//        restrictedAreaService.add(updatePatientDto.getRegionId(), PersonType.PATIENT,patient.getId());
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(patient.getTagCode())) {
            tagPatientExposeService.binding(patient.getId(),patient.getTagCode(),patient.getDepartmentId());
        }
        if (optional.isPresent()){
            Patient oldPatient = optional.get();
            Boolean update = false;
            if (Objects.nonNull(patient.getName()) && !Objects.equals(oldPatient.getName(),patient.getName())) {
                patientLogService.add(patient.getName(),LogType.UPDATE_NAME, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getBindPatientId()) && !Objects.equals(oldPatient.getBindPatientId(),patient.getBindPatientId())) {
                patientLogService.add("",LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getTagCode()) && !Objects.equals(oldPatient.getTagCode(),patient.getTagCode())) {
                patientLogService.add(patient.getTagCode(), LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getDepartmentId()) && !Objects.equals(oldPatient.getDepartmentId(),patient.getDepartmentId())) {
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(patient.getDepartmentId());
                patientLogService.add(optionalDepartment.isPresent()?optionalDepartment.get().getName():"",LogType.UPDATE_BIND_PATIENT, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getSickbedId()) && !Objects.equals(oldPatient.getSickbedId(),patient.getSickbedId())) {
                com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
                patientLogService.add(optionalWardRoomSickbed.isPresent()?optionalWardRoomSickbed.get().getBedCode():"",LogType.UPDATE_WARD, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getBirthday()) && !Objects.equals(oldPatient.getBirthday(),patient.getBirthday())) {
                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                patientLogService.add(Objects.nonNull(patient.getBirthday())?dtf2.format(patient.getBirthday()):"",LogType.UPDATE_BIRTHDAY, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getMedicalRecordNo()) && !Objects.equals(oldPatient.getMedicalRecordNo(),patient.getMedicalRecordNo())) {
                patientLogService.add(patient.getMedicalRecordNo(),LogType.UPDATE_MEDICAL_RECORD_NO, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getDisease()) && !Objects.equals(oldPatient.getDisease(),patient.getDisease())) {
                patientLogService.add(patient.getDisease(), LogType.UPDATE_DISEASE, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getRemarks()) && !Objects.equals(oldPatient.getRemarks(),patient.getRemarks())) {
                patientLogService.add(patient.getRemarks(), LogType.UPDATE_REMARKS, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getAddress()) && !Objects.equals(oldPatient.getAddress(),patient.getAddress())) {
                patientLogService.add(patient.getAddress(), LogType.UPDATE_ADDRESS, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getLevel()) && !Objects.equals(oldPatient.getLevel(),patient.getLevel())) {
                patientLogService.add(String.valueOf(patient.getLevel()), LogType.UPDATE_LEVEL, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getActionMode()) && !Objects.equals(oldPatient.getActionMode(),patient.getActionMode())) {
                patientLogService.add(patient.getActionMode().getName(), LogType.UPDATE_ACTION_MODE, userId, patient.getId());
                update = true;
            }
            if (Objects.nonNull(patient.getTimeQuantum()) && !Objects.equals(oldPatient.getTimeQuantum(),patient.getTimeQuantum())) {
                patientLogService.add(patient.getTimeQuantum(), LogType.UPDATE_TIME_QUANTUM, userId, patient.getId());
                update = true;
            }
            if (Objects.equals(update,false)){
                patientLogService.add("", LogType.UPDATE, userId, patient.getId());
            }
        }

        redisTemplate.opsForValue().set(RedisConstants.PATIENT+patient.getId(),patient,5, TimeUnit.MINUTES);
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
                patientTransferDao.deleteByPatientId(deleteDto.getId());
            });
        }
    }

    @Override
    public Page<Patient> list(Integer level, Boolean isOne, String bedCode, String keyword, String name, Boolean isLeave, Boolean isWaitLeave, LocalDate birthday, TransferState transferState, String tagCode, String medicalRecordNo, Long sickbedId, LocalDate startDateTime, LocalDate endDateTime, String cardNumber, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        if (Objects.nonNull(transferState) && Objects.equals(transferState,TransferState.TRANSFERRING)) {
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
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        }else if (Objects.nonNull(transferState) && Objects.equals(transferState,TransferState.WAITING_TO_RECEIVE)) {
            List<PatientTransfer> list2 =patientTransferDao.findByStateAndNewDepartmentIdIn(TransferState.WAITING_TO_RECEIVE,departmentIds);
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            list2.forEach(patientTransfer -> {
                ids.add(patientTransfer.getPatientId());
            });
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",ids);
            }
        }else if (Objects.nonNull(transferState) && Objects.equals(transferState,TransferState.ROUTINE)) {
            List<PatientTransfer> list =patientTransferDao.findAll();
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            if (Objects.nonNull(list) && list.size()>0) {
                list.forEach(patientTransfer -> {
                    ids.add(patientTransfer.getPatientId());
                });
            }
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.NOT_IN+"_id",ids);
            }
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        }
        if (Objects.equals(isOne,true)) {
            if (StringUtils.hasText(cardNumber)) {
                return patientDao.find(cardNumber,lionPage);
            }
        }
        if (Objects.nonNull(level)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_level",level);
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
        if (StringUtils.hasText(bedCode)) {
            List<WardRoomSickbed> list = wardRoomSickbedExposeService.find(bedCode);
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            if (Objects.nonNull(list) && list.size()>0) {
                list.forEach(wardRoomSickbed -> {
                    ids.add(wardRoomSickbed.getId());
                });
            }
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_sickbedId",ids);
            }
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

            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",LocalDateTime.of(startDateTime, LocalTime.MIN));
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",LocalDateTime.of(endDateTime, LocalTime.MAX));
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
        com.lion.core.Optional<Patient> optional = this.findById(id);
        if (optional.isEmpty()){
            return null;
        }
        PatientDetailsVo vo = new PatientDetailsVo();
        Patient patient = optional.get();
        BeanUtils.copyProperties(patient,vo);
        if (Objects.nonNull(patient.getBirthday())) {
            Period period = Period.between(patient.getBirthday(), LocalDate.now());
            vo.setAge(period.getYears());
        }
        if (Objects.nonNull(vo.getSickbedId())) {
            com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(vo.getSickbedId());
            if (optionalWardRoomSickbed.isPresent()) {
                vo.setBedCode(optionalWardRoomSickbed.get().getBedCode());
            }
        }
        if (Objects.nonNull(vo.getDepartmentId())) {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(vo.getDepartmentId());
            vo.setDepartmentName(optionalDepartment.isPresent()?optionalDepartment.get().getName():"");
        }
        vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
//        List<PatientNurse> patientNurses = patientNurseService.find(patient.getId());
//        List<PatientDetailsVo.NurseVo> nurseVos = new ArrayList<>();
//        patientNurses.forEach(patientNurse -> {
//            User nurse = userExposeService.findById(patientNurse.getNurseId());
//            PatientDetailsVo.NurseVo nurseVo = new PatientDetailsVo.NurseVo();
//            if (Objects.nonNull(nurse)){
//                nurseVo.setNurseId(nurse.getId());
//                nurseVo.setNurseName(nurse.getName());
//                nurseVo.setNurseHeadPortrait(nurse.getHeadPortrait());
//                nurseVo.setNurseHeadPortraitUrl(fileExposeService.getUrl(nurse.getHeadPortrait()));
//                nurseVos.add(nurseVo);
//            }
//        });
//        vo.setNurseVos(nurseVos);
//        List<PatientDoctor> patientDoctors = patientDoctorService.find(patient.getId());
//        List<PatientDetailsVo.DoctorVo> doctorVos = new ArrayList<>();
//        patientDoctors.forEach(patientDoctor -> {
//            User doctor = userExposeService.findById(patientDoctor.getDoctorId());
//            PatientDetailsVo.DoctorVo doctorVo = new PatientDetailsVo.DoctorVo();
//            if (Objects.nonNull(doctor)){
//                doctorVo.setDoctorId(doctor.getId());
//                doctorVo.setDoctorName(doctor.getName());
//                doctorVo.setDoctorHeadPortrait(doctor.getHeadPortrait());
//                doctorVo.setDoctorHeadPortraitUrl(fileExposeService.getUrl(doctor.getHeadPortrait()));
//                doctorVos.add(doctorVo);
//            }
//        });
//        vo.setDoctorVos(doctorVos);

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
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(patientTransfer.getNewDepartmentId());
            if (optionalDepartment.isPresent()){
                vo.setNewDepartmentName(optionalDepartment.get().getName());
            }
        }
        TempLeave tempLeave = tempLeaveDao.findFirstByPatientIdOrderByCreateDateTimeDesc(patient.getId());
        if (Objects.nonNull(tempLeave)){
            PatientDetailsVo.TempLeaveVo tempLeaveVo = new PatientDetailsVo.TempLeaveVo();
            tempLeaveVo.setUserId(tempLeave.getUserId());
            com.lion.core.Optional<User> optionalUser = userExposeService.findById(tempLeave.getUserId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                tempLeaveVo.setUserName(user.getName());
                tempLeaveVo.setHeadPortrait(user.getHeadPortrait());
                tempLeaveVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                tempLeaveVo.setStartDateTime(tempLeave.getStartDateTime());
                tempLeaveVo.setEndDateTime(tempLeave.getEndDateTime());
            }
            vo.setTempLeaveVo(tempLeaveVo);
        }

        SystemAlarm systemAlarm =  systemAlarmExposeService.findLastByPi(patient.getId());
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            vo.setAlarm(systemAlarmType.getDesc());
            vo.setAlarmType(systemAlarmType.getName());
            vo.setAlarmDataTime(systemAlarm.getDt());
            vo.setAlarmId(systemAlarm.get_id());
        }
        PatientReport patientReport = patientReportDao.findFirstByPatientIdOrderByCreateDateTimeDesc(patient.getId());
        if (Objects.nonNull(patientReport)) {
            DetailsPatientReportVo detailsPatientReportVo = new DetailsPatientReportVo();
            BeanUtils.copyProperties(patientReport,detailsPatientReportVo);
            com.lion.core.Optional<User> optionalUser = userExposeService.findById(patientReport.getReportUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                detailsPatientReportVo.setReportUserName(user.getName());
                detailsPatientReportVo.setReportUserHeadPortrait(user.getHeadPortrait());
                detailsPatientReportVo.setReportUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            vo.setPatientReport(detailsPatientReportVo);
        }


        if (Objects.nonNull(patient.getBindPatientId())){
            vo.setBindPatient(details(patient.getBindPatientId()));
        }
        return vo;
    }

    @Override
    public PatientDetailsVo detailsCardNumber(String cardNumber) {
        java.util.Optional<Patient> option = this.patientDao.findFirstByCardNumberOrderByCreateDateTimeDesc(cardNumber);
        if (option.isPresent()) {
            return details(option.get().getId());
        }
        return null;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void leave(PatientLeaveDto patientLeaveDto) {
        com.lion.core.Optional<Patient> optional = findById(patientLeaveDto.getPatientId());
        if (optional.isPresent()) {
            Patient patient = optional.get();
            patient.setId(patientLeaveDto.getPatientId());
            patient.setIsLeave(patientLeaveDto.getIsLeave());
            patient.setIsWaitLeave(false);
            patient.setLeaveRemarks(patientLeaveDto.getLeaveRemarks());
            update(patient);
            if (Objects.equals(patientLeaveDto.getIsLeave(), true)) {
                tagPatientExposeService.unbinding(patient.getId(), false);
            }
            currentPositionExposeService.delete(patient.getId(), null, null);
            patientTransferService.deleteByPatientId(patient.getId());
        }
    }

    public IPageResultData<List<ListMergeVo>> listMerge(Integer type, String name, String cardNumber, String tagCode, String medicalRecordNo, String sort, LionPage lionPage) {
        List<Long> departmentIds = new ArrayList<>();
        departmentIds = departmentExposeService.responsibleDepartment(null);
        Page<Map<String, Object>> page = this.patientDao.listMerge(type, name, cardNumber, tagCode, medicalRecordNo, sort,departmentIds , lionPage);
        List<Map<String, Object>> list = page.getContent();
        List<ListMergeVo> returnList = new ArrayList();
        list.forEach((map) -> {
            ListMergeVo vo = new ListMergeVo();
            if (Objects.nonNull(map.get("headPortrait"))){
                vo.setHeadPortrait(Long.valueOf(String.valueOf(map.get("headPortrait"))));
            }
            vo.setCreateDateTime(((Timestamp)map.get("createDateTime")).toLocalDateTime());
            vo.setTagCode(String.valueOf(map.get("tagCode")));
            vo.setType((Integer)map.get("type"));
            vo.setName(String.valueOf(map.get("name")));
            vo.setId(Long.valueOf(String.valueOf(map.get("id"))));
            if (Objects.nonNull(map.get("gender"))) {
                vo.setGender(Gender.instance(Integer.valueOf(String.valueOf(map.get("gender")))));
            }
            if (Objects.nonNull(map.get("departmentId"))) {
                Long departmentId = Long.valueOf(map.get("departmentId").toString());
                vo.setDepartmentId(departmentId);
                Optional<Department> optional = departmentExposeService.findById(departmentId);
                if (optional.isPresent()) {
                    vo.setDepartmentName(optional.get().getName());
                }
            }

            returnList.add(vo);
        });
        return new PageResultData(returnList, lionPage, page.getTotalElements());
    }

    public TodayStatisticsVo todayStatistics() {
        List<Long> departmentIds = new ArrayList<>();
        departmentIds = departmentExposeService.responsibleDepartment(null);
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        TodayStatisticsVo vo = new TodayStatisticsVo();
        vo.setTodayTemporaryPersonRegisterCount(this.temporaryPersonDao.countByCreateDateTimeGreaterThanEqualAndDepartmentIdIn(startDateTime,departmentIds ));
        vo.setTodayPatientRegisterCount(this.patientDao.countByCreateDateTimeGreaterThanEqualAndDepartmentIdIn(startDateTime,departmentIds));
        vo.setTodayRegisterCount(vo.getTodayPatientRegisterCount() + vo.getTodayTemporaryPersonRegisterCount());

        vo.setPatientNotLeaveCount(this.patientDao.countByIsLeaveIsFalseAndDepartmentIdIn(departmentIds));
        vo.setTemporaryPersonNotLeaveCount(this.temporaryPersonDao.countByIsLeaveIsFalseAndDepartmentIdIn(departmentIds));
        vo.setNotLeaveCount(vo.getPatientNotLeaveCount() + vo.getTemporaryPersonNotLeaveCount());
        return vo;
    }

    public Patient setOtherInfo(Patient patient) {
        com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
        if (optionalWardRoomSickbed.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000035"));
        }
        com.lion.core.Optional<WardRoom> optionalWardRoom = wardRoomExposeService.findById(optionalWardRoomSickbed.get().getWardRoomId());
        if (optionalWardRoom.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000036"));
        }
        com.lion.core.Optional<Ward> optionalWard = wardExposeService.findById(optionalWardRoom.get().getWardId());
        if (optionalWard.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000037"));
        }
        Ward ward = optionalWard.get();
        WardRoom wardRoom = optionalWardRoom.get();
        patient.setWardId(ward.getId());
        patient.setRoomId(wardRoom.getId());
        patient.setDepartmentId(ward.getDepartmentId());
//        if (!Objects.equals(ward.getDepartmentId(),patient.getDepartmentId())) {
//            BusinessException.throwException(MessageI18nUtil.getMessage("1000038"));
//        }
        return patient;
    }


    public void sickbedIsCanUse(Long sickbedId,Long patientId) {
        Patient patient = patientDao.findFirstBySickbedIdAndIsLeave(sickbedId,false);
        if ((Objects.isNull(patientId) && Objects.nonNull(patient)) || (Objects.nonNull(patientId) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),patientId)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000039"));
        }
    }

    private void assertMedicalRecordNoExist(String medicalRecordNo, Long id) {
        if (!StringUtils.hasText(medicalRecordNo)) {
            return;
        }
        Patient patient = patientDao.findFirstByMedicalRecordNo(medicalRecordNo);
        if ((Objects.isNull(id) && Objects.nonNull(patient)) || (Objects.nonNull(id) && Objects.nonNull(patient) && !Objects.equals(patient.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000040"));
        }
    }

    private void checkSickbedHavingRegion(Long sickbedId){
        com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(sickbedId);
        if (optionalWardRoomSickbed.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000035"));
        }
        if (Objects.isNull(optionalWardRoomSickbed.get().getRegionId())) {
            com.lion.core.Optional<WardRoom> optionalWardRoom = wardRoomExposeService.findById(optionalWardRoomSickbed.get().getWardRoomId());
            if (optionalWardRoom.isPresent()) {
                WardRoom wardRoom = optionalWardRoom.get();
                com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(wardRoom.getRegionId());
                if (Objects.isNull(wardRoom.getRegionId()) || optionalRegion.isEmpty()) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("1000049"));
                }
            }
        }else {
            com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(optionalWardRoomSickbed.get().getRegionId());
            if (optionalRegion.isEmpty()) {
                BusinessException.throwException(MessageI18nUtil.getMessage("1000049"));
            }
        }
    }

}
