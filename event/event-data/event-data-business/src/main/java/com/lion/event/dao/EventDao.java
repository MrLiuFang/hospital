package com.lion.event.dao;

import com.lion.event.entity.Alarm;
import com.lion.event.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:02
 **/
public interface EventDao extends MongoRepository<Event,String> ,EventDaoEx {
}
