package com.lion.event.service.impl;

import com.lion.event.dao.EventDao;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 上午11:13
 **/
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDao eventDao;

    @Override
    public void save(Event event) {
        eventDao.save(event);
    }

    @Override
    public List<Event> findAll() {
        return eventDao.findAll();
    }
}
