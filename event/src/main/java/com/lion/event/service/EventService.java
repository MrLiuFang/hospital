package com.lion.event.service;

import com.lion.event.entity.Event;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 上午11:13
 **/
public interface EventService {

    /**
     * 保存
     * @param event
     */
    public void save(Event event);

    /**
     * 查询所有数据
     * @return
     */
    public List<Event> findAll();
}
