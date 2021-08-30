package com.lion.event.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.event.dao.HumitureRecordDaoEx;
import com.lion.event.entity.HumitureRecord;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午11:22
 */
@Log
public class HumitureRecordDaoImpl implements HumitureRecordDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public HumitureRecord find(Long tagId,Boolean isPrevious) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(tagId)) {
            criteria.and("ti").is(tagId);
        }
        LocalDateTime now = LocalDateTime.now();
        criteria.andOperator( Criteria.where("ddt").gte(now.minusDays(30)) ,Criteria.where("ddt").lte(now));
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(0,2,Sort.by(Sort.Order.desc("ddt")));
        query.with(pageRequest);
        List<HumitureRecord> items = mongoTemplate.find(query, HumitureRecord.class);
        if (Objects.nonNull(items) && items.size()>0){
            if (Objects.equals(true,isPrevious) && items.size()==2) {
                return items.get(1);
            }else if (Objects.equals(false,isPrevious)){
                return items.get(0);
            }
        }
        return null;
    }
}
