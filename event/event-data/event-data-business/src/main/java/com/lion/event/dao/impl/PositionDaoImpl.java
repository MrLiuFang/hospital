package com.lion.event.dao.impl;

import com.lion.common.enums.Type;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.event.dao.PositionDaoEx;
import com.lion.event.entity.Position;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import lombok.extern.java.Log;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午5:10
 */
@Log
public class PositionDaoImpl implements PositionDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Position> find(Long id, Type type, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if ((Objects.equals(type,Type.STAFF) ||Objects.equals(type,Type.PATIENT) || Objects.equals(type,Type.MIGRANT)) && Objects.nonNull(id)) {
            criteria.and("pi").is(id);
        }else if (Objects.equals(type,Type.ASSET) && Objects.nonNull(id)) {
            criteria.and("adi").is(id);
        }else if (( Objects.equals(type,Type.TEMPERATURE) || Objects.equals(type,Type.HUMIDITY) )  && Objects.nonNull(id)) {
            criteria.and("ti").is(id);
        }else {
            return null;
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator(Criteria.where("ddt").gte(startDateTime), Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("ddt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("ddt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(0,99999999, Sort.by(Sort.Order.desc("ddt")));
        query.with(pageRequest);
        List<Position> items = mongoTemplate.find(query,Position.class);
        List<Position> list = new ArrayList<>();
        if (Objects.nonNull(items) && items.size()>0){
            items.forEach(position -> {
                list.add(position);
            });
        }
        return list;
    }

    @Override
    public List<String> personAllRegion(Long personId, Long regionId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$rn"); //区域

        BasicDBObject match = new BasicDBObject();
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(personId)){
            match = BasicDBObjectUtil.put(match,"$match","pi",personId );
        }
        if (Objects.nonNull(regionId)){
            match = BasicDBObjectUtil.put(match,"$match","ri",regionId );
        }
        pipeline.add(match);
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("position").aggregate(pipeline);
        List<String> returnList = new ArrayList<>();
        aggregateIterable.forEach(d -> {
            returnList.add(d.getString("_id"));
        });
        return returnList;
    }
}
