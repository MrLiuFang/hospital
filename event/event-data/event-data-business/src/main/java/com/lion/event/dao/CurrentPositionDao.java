package com.lion.event.dao;

import com.lion.event.entity.Alarm;
import com.lion.event.entity.CurrentPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午3:01
 **/
public interface CurrentPositionDao extends MongoRepository<CurrentPosition,String> {
}
