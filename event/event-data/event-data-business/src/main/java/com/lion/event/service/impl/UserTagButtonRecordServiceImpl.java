package com.lion.event.service.impl;

import com.lion.event.dao.UserTagButtonRecordDao;
import com.lion.event.entity.UserTagButtonRecord;
import com.lion.event.service.UserTagButtonRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:36
 */
@Service
public class UserTagButtonRecordServiceImpl implements UserTagButtonRecordService {

    @Autowired
    private UserTagButtonRecordDao userTagButtonRecordDao;

    @Override
    public void add(UserTagButtonRecord userTagButtonRecord) {
        userTagButtonRecordDao.save(userTagButtonRecord);
    }
}
