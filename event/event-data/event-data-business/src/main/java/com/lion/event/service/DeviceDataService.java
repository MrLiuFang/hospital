package com.lion.event.service;

import com.lion.event.entity.DeviceData;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 上午11:37
 **/
public interface DeviceDataService {

    /**
     * 保存设备事件数据
     * @param deviceData
     */
    public void save(DeviceData deviceData);
}
