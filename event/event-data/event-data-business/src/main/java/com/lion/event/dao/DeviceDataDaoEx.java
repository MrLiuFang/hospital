package com.lion.event.dao;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.DeviceData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/6 上午11:43
 **/
public interface DeviceDataDaoEx {

    /**
     * 列表
     * @param starCode
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<DeviceData>> list(String starCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
