package com.lion.event.service;

import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.person.entity.person.Patient;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 上午11:34
 */
public interface PatientService {

    /**
     * 处理患者事件
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param patient
     */
    public void patientEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, Patient patient);
}
