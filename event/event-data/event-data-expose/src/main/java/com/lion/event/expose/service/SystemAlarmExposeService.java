package com.lion.event.expose.service;

import com.lion.event.entity.SystemAlarm;

public interface SystemAlarmExposeService {

    /**
     * 查找最后一次资产警告
     * @param assetsId
     * @return
     */
    SystemAlarm findLastByAssetsId(Long  assetsId);

    /**
     * 查找最后一次tag警告
     * @param tagId
     * @return
     */
    SystemAlarm findLastByTagId(Long  tagId);

    /**
     *
     * @param type
     * @return
     */
    String getSystemAlarmTypeCode(Integer type);

    /**
     *
     * @param type
     * @return
     */
    String getSystemAlarmTypeDesc(Integer type);
}
