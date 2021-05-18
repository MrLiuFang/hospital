package com.lion.event.service;

import com.lion.event.entity.SystemAlarm;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
public interface SystemAlarmService {

    public void save(SystemAlarm systemAlarm);

    /**
     * 更新显示排序时间
     * @param uuid
     */
    public void updateSdt(String uuid);

    /**
     * 解除警告
     * @param uuid
     */
    void unalarm(String uuid);
}
