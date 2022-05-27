package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.PatientState;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午2:38
 */
public interface PatientExposeService extends BaseService<Patient> {

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateState(Long id,Integer state);

    /**
     * 修改病人状态
     * @param id
     * @param patientState
     */
    public void updatePatientState(Long id, PatientState patientState);

    /**
     * 修改状态
     * @param id
     * @param isWaitLeave
     */
    public void updateIsWaitLeave(Long id,Boolean isWaitLeave);

    /**
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);

    /**
     * 统计
     * @param departmentId
     * @param deviceState
     * @param ids
     * @return
     */
    public int count(Long departmentId, State deviceState, List<Long> ids);

    /**
     * 根据部门查询患者
     * @param departmentId
     * @param name
     * @param ids
     * @return
     */
    public List<Patient> find(Long departmentId,String name,List<Long> ids);

    /**
     * 统计被使用的床位
     * @param sickbedIds
     * @return
     */
    public int countUseSickbed(List<Long> sickbedIds);

    public List<Patient> find(Boolean isLeave);


}
