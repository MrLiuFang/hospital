package com.lion.event.dao.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.DeviceDataDaoEx;
import com.lion.event.entity.DeviceData;
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
 * @Date 2021/5/6 上午11:44
 **/
public class DeviceDataDaoImpl implements DeviceDataDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public IPageResultData<List<DeviceData>> list(String starCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(starCode)) {
            criteria.and("sc").is(starCode);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
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
        List<DeviceData> items = mongoTemplate.find(query,DeviceData.class);
//        long count = mongoTemplate.count(query, Wash.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        IPageResultData<List<DeviceData>> pageResultData =new PageResultData<>(items,lionPage,0L);
        return pageResultData;
    }
}
