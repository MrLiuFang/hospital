package com.lion.event.dao;

import com.lion.event.entity.WashRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:44
 **/
public interface WashRecordDao extends MongoRepository<WashRecord,String> , WashRecordDaoEx {

}
