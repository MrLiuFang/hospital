package com.lion.event.dao;

import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.UserTagButtonRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:36
 */
public interface UserTagButtonRecordDao extends MongoRepository<UserTagButtonRecord,String> {
}
