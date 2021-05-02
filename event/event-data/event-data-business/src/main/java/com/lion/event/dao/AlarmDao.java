package com.lion.event.dao;

import com.lion.event.entity.Alarm;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:00
 **/
public interface AlarmDao extends MongoRepository<Alarm,String> {
}
