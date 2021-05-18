package com.lion.event.dao.impl;

import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.core.LionPage;
import com.lion.event.dao.WashEventDaoEx;
import com.lion.event.entity.WashEvent;
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
public class WashEventDaoImpl implements WashEventDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void updateWt(String uuid, LocalDateTime wt) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        WashEvent washEvent = mongoTemplate.findOne(query, WashEvent.class);
        if (Objects.nonNull(washEvent) && Objects.equals(true,washEvent.getIa()) ) {
            query = new Query();
            query.addCriteria(Criteria.where("_id").is(washEvent.get_id()));
            Update update = new Update();
            update.set("wt", wt);
            mongoTemplate.updateFirst(query, update, "wash_event");
        }
    }

    @Override
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, UserType userType, Long userId, LionPage lionPage) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        if (Objects.nonNull(isDepartmentGroup) && Objects.equals(true,isDepartmentGroup)) {
            group = BasicDBObjectUtil.put(group,"$group","_id","$pdn"); //部门分组
        }else if (Objects.nonNull(isDepartmentGroup)){
            group = BasicDBObjectUtil.put(group,"$group","_id","$a"); //全院
        }else if (Objects.isNull(isDepartmentGroup)){
            group = BasicDBObjectUtil.put(group,"$group","_id","$pi"); //员工分组
        }
        group = BasicDBObjectUtil.put(group,"$group","allCount",new BasicDBObject("$sum",1));//全部
        LocalDateTime wt = LocalDateTime.parse("9997-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        group = BasicDBObjectUtil.put(group,"$group","allNoAlarm",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",false})})).append("then",1).append("else",0))));//合规
        group = BasicDBObjectUtil.put(group,"$group","allViolation",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",true}),new BasicDBObject("$lte",new Object[]{"$wt", wt}) })).append("then",1).append("else",0))));//违规
        group = BasicDBObjectUtil.put(group,"$group","allNoWash",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",true}),new BasicDBObject("$gte",new Object[]{"$wt",wt}) })).append("then",1).append("else",0))));//错过洗手

        BasicDBObject match = new BasicDBObject();
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$lte",endDateTime));
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

        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("wash_event").aggregate(pipeline);
        List<Document> list = new ArrayList<>();
        aggregateIterable.forEach(document -> {
            list.add(document);
        });
        return list;

//        db.event.aggregate( [
//        {
//            $group: {
//                _id: "$pi",
//                allCount: { $sum: 1 },
//                allNoAlarm: { "$sum": {$cond:{if:{$eq:["$ia",false]},then:1,else:0}}},
//                allViolation: { "$sum": {$cond:{if:{$and:[ {$eq:["$ia",true]},{ "$lte": ["$uadt",ISODate("9990-12-31T16:00:00Z")]} ]},then:1,else:0}}},
//                allNoWash: { "$sum": {$cond:{if:{$and:[ {$eq:["$ia",true]},{ "$gte": ["$uadt",ISODate("9990-12-31T16:00:00Z")]} ]},then:1,else:0}}}
//            }
//        },
//        {
//            $project:{
//                _id: 1,
//                allNoAlarmRatio:{ $divide:["$allNoAlarm","$allCount"]},
//                allViolationRatio:{ $divide:["$allViolation","$allCount"]},
//                allNoWashRatio:{ $divide:["$allNoWash","$allCount"]},
//            }
//        },
//        { $match: { allNoAlarmRatio: {$lt:80}} },
// ] )
    }

    @Override
    public List<WashEvent> userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
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
        List<WashEvent> items = mongoTemplate.find(query, WashEvent.class);
        return items;
    }
}
