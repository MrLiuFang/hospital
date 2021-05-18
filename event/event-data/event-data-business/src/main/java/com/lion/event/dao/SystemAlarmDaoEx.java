package com.lion.event.dao;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:22
 **/
public interface SystemAlarmDaoEx {

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
