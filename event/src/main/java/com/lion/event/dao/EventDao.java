package com.lion.event.dao;

import com.lion.event.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 上午11:12
 **/
public interface EventDao extends MongoRepository<Event,String> {
}
