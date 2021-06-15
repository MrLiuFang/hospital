package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.upms.entity.user.User;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 上午11:29
 */
public interface UserButtonService {

    /**
     * 处理员工按钮事件
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param user
     */
    public void tagButtonEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, User user) throws JsonProcessingException;
}
