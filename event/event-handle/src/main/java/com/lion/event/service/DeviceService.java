package com.lion.event.service;

import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.upms.entity.user.User;

/**
 * @Author Mr.Liu
 * @Description //资产,温湿仪设备数据处理
 * @Date 2021/5/15 上午10:52
 **/
public interface DeviceService {

    /**
     * 设备数据处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     */
    public void deviceEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag);

}
