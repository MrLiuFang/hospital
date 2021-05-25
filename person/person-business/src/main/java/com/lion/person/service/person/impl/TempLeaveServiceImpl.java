package com.lion.person.service.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.TempLeaveDao;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddTempLeaveDto;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.TempLeaveService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            });

        }
    }
}
