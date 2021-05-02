package com.lion.event.service;

import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.upms.entity.user.User;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:45
 **/
public interface UserWashService {

    /**
     * 用户洗手事件处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param user
     */
    public void userWashEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, User user);
}
