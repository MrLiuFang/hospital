package com.lion.event.dao;

import com.lion.event.entity.Position;
import com.lion.event.entity.RecyclingBoxRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:50
 */
public interface RecyclingBoxRecordDao extends MongoRepository<RecyclingBoxRecord,String> {
}
