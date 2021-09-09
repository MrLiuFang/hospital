package com.lion.event.dao;

import com.lion.event.entity.HumitureRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午9:18
 **/
public interface HumitureRecordDao extends MongoRepository<HumitureRecord,String> , HumitureRecordDaoEx {

}
