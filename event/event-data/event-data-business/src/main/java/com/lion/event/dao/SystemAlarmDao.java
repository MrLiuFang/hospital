package com.lion.event.dao;

import com.lion.event.entity.SystemAlarm;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:00
 **/
public interface SystemAlarmDao extends MongoRepository<SystemAlarm,String> ,SystemAlarmDaoEx {

}
