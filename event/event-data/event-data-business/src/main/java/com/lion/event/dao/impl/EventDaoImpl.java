package com.lion.event.dao.impl;

import com.lion.common.enums.Type;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.core.LionPage;
import com.lion.event.dao.EventDaoEx;
import com.lion.event.entity.Event;
import com.lion.upms.entity.enums.UserType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import lombok.extern.java.Log;
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
@Log
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
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, UserType userType, Long userId, LionPage lionPage) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        if (Objects.nonNull(isDepartmentGroup) && Objects.equals(true,isDepartmentGroup)) {
            group = BasicDBObjectUtil.put(group,"$group","_id","$pdn"); //部门分组
        }else if (Objects.nonNull(isDepartmentGroup)){
            group = BasicDBObjectUtil.put(group,"$group","_id","$typ"); //全院
        }else if (Objects.isNull(isDepartmentGroup)){
            group = BasicDBObjectUtil.put(group,"$group","_id","$pi"); //员工分组
        }
        group = BasicDBObjectUtil.put(group,"$group","allCount",new BasicDBObject("$sum",1));//全部
        LocalDateTime uadt = LocalDateTime.parse("9998-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        group = BasicDBObjectUtil.put(group,"$group","allNoAlarm",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",false})})).append("then",1).append("else",0))));//合规
        group = BasicDBObjectUtil.put(group,"$group","allViolation",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",true}),new BasicDBObject("$lte",new Object[]{"$uadt", uadt}) })).append("then",1).append("else",0))));//违规
        group = BasicDBObjectUtil.put(group,"$group","allNoWash",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",true}),new BasicDBObject("$gte",new Object[]{"$uadt",uadt}) })).append("then",1).append("else",0))));//错过洗手

        BasicDBObject match = new BasicDBObject();
        match = BasicDBObjectUtil.put(match,"$match","typ",new BasicDBObject("$eq",Type.STAFF.getKey()) );
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","sdt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(userType)){
            match = BasicDBObjectUtil.put(match,"$match","py",new BasicDBObject("$eq",userType.getKey()) );
        }
        if (Objects.nonNull(userId)){
            match = BasicDBObjectUtil.put(match,"$match","pi",new BasicDBObject("$eq",userId) );
        }

        BasicDBObject project = new BasicDBObject();
        project = BasicDBObjectUtil.put(project,"$project","_id",1);
        project = BasicDBObjectUtil.put(project,"$project","allNoAlarmRatio",new BasicDBObject("$divide",new String[]{"$allNoAlarm","$allCount"}));
        project = BasicDBObjectUtil.put(project,"$project","allViolationRatio",new BasicDBObject("$divide",new String[]{"$allViolation","$allCount"}));
        project = BasicDBObjectUtil.put(project,"$project","allNoWashRatio",new BasicDBObject("$divide",new String[]{"$allNoWash","$allCount"}));

        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(project);

        if (Objects.nonNull(userType)){
            BasicDBObject having = new BasicDBObject();
            having = BasicDBObjectUtil.put(having,"$match","allNoAlarmRatio",new BasicDBObject("$lt",80));
            pipeline.add(having);
        }

        if (Objects.nonNull(lionPage)) {
            pipeline.add(new BasicDBObject("$skip",lionPage.getPageNumber()*lionPage.getPageSize()));
            pipeline.add(new BasicDBObject("$limit",lionPage.getPageSize()));
        }

        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("event").aggregate(pipeline);
        List<Document> list = new ArrayList<>();
        aggregateIterable.forEach(document -> {
            list.add(document);
        });
        return list;
    }

    @Override
    public List<Event> userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria.and("pi").is(userId);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("sdt").gte(startDateTime) ,Criteria.where("sdt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("sdt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("sdt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        List<Event> items = mongoTemplate.find(query,Event.class);
        return items;
    }
}
