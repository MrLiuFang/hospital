package com.lion.event.dao;

import com.lion.event.entity.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:02
 **/
public interface PositionDao extends MongoRepository<Position,String>,PositionDaoEx {
}
