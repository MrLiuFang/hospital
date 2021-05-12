package com.lion.event.dao.impl;

import com.lion.common.enums.Type;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.event.dao.EventDaoEx;
import com.lion.event.entity.Event;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/11 下午3:09
 **/
public class EventDaoImpl implements EventDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void updateUadt(String uuid, LocalDateTime uadt) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        Event event = mongoTemplate.findOne(query, Event.class);
        query = new Query();
        query.addCriteria(Criteria.where("_id").is(event.get_id()));
        Update update = new Update();
        update.set("uadt",uadt);
        mongoTemplate.updateFirst(query, update,"event");
    }

    @Override
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, Boolean isAlarm, Boolean isNoWash) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        if (Objects.equals(true,isDepartmentGroup)) {
            group = BasicDBObjectUtil.put(group,"$group","_id","$pdn");
        }else {
            group = BasicDBObjectUtil.put(group,"$group","_id","$typ");
        }
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        BasicDBObject match = new BasicDBObject();
        match = BasicDBObjectUtil.put(match,"$match","typ",Type.STAFF.getKey());
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(isAlarm) ) {
            match = BasicDBObjectUtil.put(match, "$match", "ia", Objects.equals(true, isAlarm));
        }

        if (Objects.equals(true,isAlarm) && Objects.equals(true,isNoWash)) {
            match = BasicDBObjectUtil.put(match,"$match","uadt",new BasicDBObject("$gte",LocalDateTime.parse("9997-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }else if (Objects.equals(true,isAlarm) ){
            match = BasicDBObjectUtil.put(match,"$match","uadt",new BasicDBObject("$lte",LocalDateTime.parse("9996-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        pipeline.add(match);
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("event").aggregate(pipeline);
        List<Document> list = new ArrayList<>();
        aggregateIterable.forEach(document -> {
            list.add(document);
        });
        return list;
    }
}
