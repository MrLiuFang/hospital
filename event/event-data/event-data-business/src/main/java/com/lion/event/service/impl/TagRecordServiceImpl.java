package com.lion.event.service.impl;

import com.lion.event.dao.TagRecordDao;
import com.lion.event.entity.TagRecord;
import com.lion.event.service.TagRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:17
 **/
@Service
public class TagRecordServiceImpl implements TagRecordService {

    @Autowired
    private TagRecordDao tagRecordDao;

    @Override
    public void save(TagRecord tagRecord) {
        tagRecordDao.save(tagRecord);
    }
}
