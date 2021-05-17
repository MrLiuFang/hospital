package com.lion.event.dao;

import com.lion.event.entity.TagRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:18
 **/
public interface TagRecordDao extends MongoRepository<TagRecord,String> {
}
