package com.lion.event.service.impl;

import com.lion.event.dao.AlarmDao;
import com.lion.event.entity.Alarm;
import com.lion.event.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmDao alarmDao;

    @Override
    public void save(Alarm alarm) {
        alarmDao.save(alarm);
    }
}
