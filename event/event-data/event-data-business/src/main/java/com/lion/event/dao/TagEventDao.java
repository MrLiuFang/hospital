package com.lion.event.dao;

import com.lion.event.entity.TagEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:19
 **/
public interface TagEventDao extends MongoRepository<TagEvent,String> {
}
