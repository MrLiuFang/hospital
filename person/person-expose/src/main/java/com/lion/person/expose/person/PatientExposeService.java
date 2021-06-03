package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
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
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);

    /**
     * 统计
     * @param departmentId
     * @param deviceSate
     * @return
     */
    public int count(Long departmentId, State deviceSate);

    /**
     * 根据部门查询患者
     * @param departmentId
     * @return
     */
    public List<Patient> find(Long departmentId,String name);
}