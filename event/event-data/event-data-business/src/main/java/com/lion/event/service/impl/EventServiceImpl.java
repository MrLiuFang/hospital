package com.lion.event.service.impl;

import com.lion.event.dao.EventDao;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:11
 **/
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDao eventDao;

    @Override
    public void save(Event event) {
        eventDao.save(event);
    }
}
