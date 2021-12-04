package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.person.dao.person.TempLeaveDao;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddTempLeaveDto;
import com.lion.person.entity.person.dto.AdvanceOverTempLeaveDto;
import com.lion.person.entity.person.vo.ListTempLeaveVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.TempLeaveService;
import com.lion.person.utils.ExcelColumn;
import com.lion.person.utils.ExportExcelUtil;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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

    @Autowired
    private HttpServletResponse response;

    @Override
    @Transactional
    public void addTempLeave(AddTempLeaveDto addTempLeaveDto) {
        User user = userExposeService.find(addTempLeaveDto.getNumber());
        if (Objects.isNull(user)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000047"));
        }
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
                TempLeave tempLeave = new TempLeave();
                tempLeave.setUserId(user.getId());
                tempLeave.setPatientId(id);
                tempLeave.setStartDateTime(addTempLeaveDto.getStartDateTime());
                tempLeave.setEndDateTime(addTempLeaveDto.getEndDateTime());
                tempLeave.setRemarks(addTempLeaveDto.getRemarks());
                save(tempLeave);
                patientLogService.add("新增临时离开权限", LogType.ADD_TEMP_LEAVE, CurrentUserUtil.getCurrentUserId(),id);
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

    @Override
    public void export(String tagCode, Long departmentId, Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException, IllegalAccessException {
        IPageResultData<List<ListTempLeaveVo>> pageResultData = list(tagCode,departmentId,patientId,userId,startDateTime,endDateTime,new LionPage(0,Integer.MAX_VALUE));
        List<ListTempLeaveVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("patient name", "patientName"));
        excelColumn.add(ExcelColumn.build("gender", "gender"));
        excelColumn.add(ExcelColumn.build("age", "age"));
        excelColumn.add(ExcelColumn.build("medical record no", "medicalRecordNo"));
        excelColumn.add(ExcelColumn.build("department name", "departmentName"));
        excelColumn.add(ExcelColumn.build("disease", "disease"));
        excelColumn.add(ExcelColumn.build("leave datetime", "startDateTime"));
        excelColumn.add(ExcelColumn.build("registrant", "userName"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("tempLeave.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }
}
