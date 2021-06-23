package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.person.dao.person.TempLeaveDao;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddTempLeaveDto;
import com.lion.person.entity.person.dto.AdvanceOverTempLeaveDto;
import com.lion.person.entity.person.vo.ListTempLeaveVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.TempLeaveService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:24
 */
@Service
public class TempLeaveServiceImpl extends BaseServiceImpl<TempLeave> implements TempLeaveService {

    @Autowired
    private TempLeaveDao tempLeaveDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private PatientService patientService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private PatientLogService patientLogService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Override
    public void addTempLeave(AddTempLeaveDto addTempLeaveDto) {
        User user = userExposeService.find(addTempLeaveDto.getNumber());
        if (Objects.isNull(user)){
            BusinessException.throwException("该授权人不存在");
        }
        TempLeave tempLeave = new TempLeave();
        tempLeave.setUserId(user.getId());
        if (Objects.nonNull(addTempLeaveDto.getPatientIds()) && addTempLeaveDto.getPatientIds().size()>0) {
//            addTempLeaveDto.getPatientIds().forEach(id ->{
//                TempLeave temp = tempLeaveDao.findFirstByIsClosureAndOrPatientIdAndStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(false,id,addTempLeaveDto.getStartDateTime(),addTempLeaveDto.getEndDateTime());
//                if (Objects.nonNull(temp)) {
//                    Patient patient = patientService.findById(temp.getPatientId());
//                    if (Objects.nonNull(patient)){
//                        BusinessException.throwException(patient.getName()+"存在临时离开的权限");
//                    }
//                }
//            });
            addTempLeaveDto.getPatientIds().forEach(id ->{
                tempLeave.setPatientId(id);
                tempLeave.setStartDateTime(addTempLeaveDto.getStartDateTime());
                tempLeave.setEndDateTime(addTempLeaveDto.getEndDateTime());
                tempLeave.setRemarks(addTempLeaveDto.getRemarks());
                save(tempLeave);
                patientLogService.add("新增临时离开权限",id);
            });

        }
    }

    @Override
    @Transactional
    public void advanceOverTempLeave(AdvanceOverTempLeaveDto advanceOverTempLeaveDto) {
        if (Objects.nonNull(advanceOverTempLeaveDto.getPatientIds()) && advanceOverTempLeaveDto.getPatientIds().size()>0 ) {
            advanceOverTempLeaveDto.getPatientIds().forEach(id->{
                TempLeave tempLeave = tempLeaveDao.findFirstByPatientIdOrderByCreateDateTimeDesc(id);
                tempLeave.setIsClosure(true);
                update(tempLeave);
            });
        }
    }

    @Override
    public IPageResultData<List<ListTempLeaveVo>> list(String tagCode, Long departmentId, Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Page<TempLeave> page = tempLeaveDao.list(tagCode, departmentId, patientId, userId, startDateTime, endDateTime, lionPage);
        List<TempLeave> list = page.getContent();
        List<ListTempLeaveVo> returnList = new ArrayList<>();
        list.forEach(tempLeave -> {
            ListTempLeaveVo vo = new ListTempLeaveVo();
            BeanUtils.copyProperties(tempLeave,vo);
            Patient patient =patientService.findById(tempLeave.getPatientId());
            if (Objects.nonNull(patient)){
                vo.setPatientName(patient.getName());
                vo.setGender(patient.getGender());
                if (Objects.nonNull(patient.getBirthday())) {
                    Period period = Period.between(patient.getBirthday(), LocalDate.now());
                    vo.setAge(period.getYears());
                }
                vo.setBirthday(patient.getBirthday());
                vo.setDisease(patient.getDisease());
                vo.setMedicalRecordNo(patient.getMedicalRecordNo());
                vo.setTagCode(patient.getTagCode());
                vo.setHeadPortrait(patient.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
            }
            User user = userExposeService.findById(tempLeave.getUserId());
            if (Objects.nonNull(user)){
                vo.setUserName(user.getName());
                vo.setUserHeadPortrait(user.getHeadPortrait());
                vo.setUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            Department department = departmentExposeService.findById(patient.getDepartmentId());
            if (Objects.nonNull(department)) {
                vo.setDepartmentName(department.getName());
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }
}
