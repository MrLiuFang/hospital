package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:30
 */
public interface RecyclingBoxService {

    /**
     * 回收箱事件处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param patient
     * @param temporaryPerson
     */
    public void event(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, Patient patient, TemporaryPerson temporaryPerson) throws JsonProcessingException;
}
