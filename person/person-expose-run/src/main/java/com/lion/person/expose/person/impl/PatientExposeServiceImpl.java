package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientDao;
import com.lion.person.entity.enums.PatientState;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import com.lion.person.expose.person.PatientExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午2:41
 */
@DubboService(interfaceClass = PatientExposeService.class)
public class PatientExposeServiceImpl extends BaseServiceImpl<Patient> implements PatientExposeService {

    @Autowired
    private PatientDao patientDao;

    @Override
    public void updateState(Long id, Integer state) {
        if (Objects.equals(state,State.NORMAL.getKey())) {
            patientDao.updateState1(id, State.instance(state));
        }else {
            patientDao.updateState(id, State.instance(state));
        }
    }

    @Override
    public void updatePatientState(Long id, PatientState patientState) {
        if (Objects.isNull(patientState)) {
            patientDao.updatePatientStateIsNull(id);
        }
        patientDao.updatePatientState(id,patientState);
    }

    @Override
    public void updateIsWaitLeave(Long id, Boolean isWaitLeave) {
        patientDao.updateIsWaitLeave(id,isWaitLeave);
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        patientDao.updateLastDataTime(id,dateTime);
    }

    @Override
    public int count(Long departmentId, State deviceState, List<Long> ids) {
        if (Objects.isNull(deviceState) && (Objects.isNull(ids) || ids.size()<=0)){
            return patientDao.countByDepartmentIdAndIsLeave(departmentId,false);
        }else if (Objects.isNull(deviceState) && (Objects.nonNull(ids) || ids.size()>0)){
            return patientDao.countByDepartmentIdAndIsLeaveAndIdIn(departmentId,false,ids);
        }
        if (Objects.nonNull(deviceState) && (Objects.isNull(ids) || ids.size()<=0)){
            return patientDao.countByDepartmentIdAndIsLeaveAndDeviceState(departmentId,false,deviceState);
        }else{
            return patientDao.countByDepartmentIdAndIsLeaveAndDeviceStateAndIdIn(departmentId,false,deviceState,ids);
        }
    }

    @Override
    public List<Patient> find(Long departmentId, String name, List<Long> ids) {
        if (StringUtils.hasText(name) && (Objects.isNull(ids)|| ids.size()<=0 )  ){
            return patientDao.findByDepartmentIdAndIsLeaveAndNameLikeOrderByPatientStateDesc(departmentId,false,"%"+name+"%");
        }else if (StringUtils.hasText(name) && (Objects.nonNull(ids)|| ids.size()>0 )  ){
            return patientDao.findByDepartmentIdAndIsLeaveAndNameLikeAndIdIn(departmentId,false,"%"+name+"%",ids);
        }
        if (!StringUtils.hasText(name) && (Objects.isNull(ids)|| ids.size()<=0 )  ) {
            return patientDao.findByDepartmentIdAndIsLeaveOrderByPatientStateDesc(departmentId, false);
        }else if (!StringUtils.hasText(name) && (Objects.nonNull(ids)|| ids.size()>0 )  ) {
            return patientDao.findByDepartmentIdAndIsLeaveAndIdIn(departmentId, false,ids);
        }

        return patientDao.findByDepartmentIdAndIsLeaveOrderByPatientStateDesc(departmentId, false);
    }

    @Override
    public int countUseSickbed(List<Long> sickbedIds) {
        return patientDao.countBySickbedIdInAndIsLeave(sickbedIds,false);
    }

    @Override
    public List<Patient> find(Boolean isLeave) {
        return patientDao.findByIsLeave(isLeave);
    }

}
