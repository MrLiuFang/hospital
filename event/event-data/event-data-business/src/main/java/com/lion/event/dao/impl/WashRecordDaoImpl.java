package com.lion.event.dao.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.WashRecordDaoEx;
import com.lion.event.entity.WashRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 下午2:59
 **/
public class WashRecordDaoImpl implements WashRecordDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IPageResultData<List<WashRecord>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria.and("pi").is(userId);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime) ,Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("ddt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("ddt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        List<WashRecord> items = mongoTemplate.find(query, WashRecord.class);
//        long count = mongoTemplate.count(query, Wash.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        IPageResultData<List<WashRecord>> pageResultData =new PageResultData<>(items,lionPage,0L);
        return pageResultData;
    }
}
