package com.lion.event.dao;

import com.lion.event.entity.Wash;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:44
 **/
public interface WashDao extends MongoRepository<Wash,String> ,WashDaoEx{

}
