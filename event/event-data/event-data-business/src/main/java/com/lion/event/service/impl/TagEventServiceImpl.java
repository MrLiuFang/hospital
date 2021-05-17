package com.lion.event.service.impl;

import com.lion.event.dao.TagEventDao;
import com.lion.event.entity.TagEvent;
import com.lion.event.service.TagEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:17
 **/
@Service
public class TagEventServiceImpl implements TagEventService {

    @Autowired
    private TagEventDao tagEventDao;

    @Override
    public void save(TagEvent tagEvent) {
        tagEventDao.save(tagEvent);
    }
}
