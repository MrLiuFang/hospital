package com.lion.event.dao;

import com.lion.event.entity.WashEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:02
 **/
public interface WashEventDao extends MongoRepository<WashEvent,String> , WashEventDaoEx {
}
