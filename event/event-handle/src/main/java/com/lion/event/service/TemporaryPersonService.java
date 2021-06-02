package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.person.entity.person.TemporaryPerson;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 下午2:59
 */
public interface TemporaryPersonService {

    /**
     * 流动人员事件处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param temporaryPerson
     */
    public void  TemporaryPersonEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, TemporaryPerson temporaryPerson) throws JsonProcessingException;
}
