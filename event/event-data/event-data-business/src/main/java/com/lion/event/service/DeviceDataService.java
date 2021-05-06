package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.DeviceData;
import com.lion.event.entity.Wash;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 列表
     * @param starId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<DeviceData>> list(Long starId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
