package com.lion.event.service.impl;

import com.lion.event.dao.RecyclingBoxRecordDao;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.service.RecyclingBoxRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:50
 */
@Service
public class RecyclingBoxRecordServiceImpl implements RecyclingBoxRecordService {

    @Autowired
    private RecyclingBoxRecordDao recyclingBoxRecordDao;

    @Override
    public void save(RecyclingBoxRecord recyclingBoxRecord) {
        recyclingBoxRecordDao.save(recyclingBoxRecord);
    }
}
