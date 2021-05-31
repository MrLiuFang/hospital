package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.TempLeaveDao;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddTempLeaveDto;
import com.lion.person.entity.person.dto.AdvanceOverTempLeaveDto;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.ListTempLeaveVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.TempLeaveService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    private UserExposeService userExposeService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private FileExposeService fileExposeService;

    @Autowired
    private PatientLogService patientLogService;

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
    public void advanceOverTempLeave(AdvanceOverTempLeaveDto advanceOverTempLeaveDto) {
        TempLeave tempLeave = tempLeaveDao.findFirstByPatientIdOrderByCreateDateTimeDesc(advanceOverTempLeaveDto.getPatientId());
        tempLeave.setIsClosure(true);
        update(tempLeave);
    }

    @Override
    public IPageResultData<List<ListTempLeaveVo>> list(Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(patientId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_patientId",patientId);
        }
        if (Objects.nonNull(userId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_userId",userId);
        }
        if (Objects.nonNull(startDateTime)) {
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_startDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)) {
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_endDateTime",endDateTime);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TempLeave> page = this.findNavigator(lionPage);
        List<TempLeave> list = page.getContent();
        List<ListTempLeaveVo> returnList = new ArrayList<>();
        list.forEach(tempLeave -> {
            ListTempLeaveVo vo = new ListTempLeaveVo();
            BeanUtils.copyProperties(tempLeave,vo);
            Patient patient =patientService.findById(tempLeave.getPatientId());
            if (Objects.nonNull(patient)){
                vo.setPatientName(patient.getName());
                vo.setHeadPortrait(patient.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
            }
            User user = userExposeService.findById(tempLeave.getUserId());
            if (Objects.nonNull(user)){
                vo.setUserName(user.getName());
                vo.setUserHeadPortrait(user.getHeadPortrait());
                vo.setUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }
}