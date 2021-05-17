package com.lion.event.service;

import com.lion.event.entity.Position;
import com.lion.event.entity.SystemAlarm;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午3:01
 **/
public interface CurrentPositionService  {

    /**
     * 保存当前位置
     * @param position
     */
    public void save(Position position);

}
