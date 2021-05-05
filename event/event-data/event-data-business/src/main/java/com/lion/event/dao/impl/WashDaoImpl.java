package com.lion.event.dao.impl;

import com.lion.event.dao.WashDaoEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 下午2:59
 **/
public class WashDaoImpl implements WashDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;
}
