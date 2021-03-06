package com.lion.event.dao.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.WashEventDaoEx;
import com.lion.event.entity.WashEvent;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author Mr.Liu
 * @Description
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
        if (Objects.nonNull(washEvent) && Objects.equals(true,washEvent.getIa()) && washEvent.getWt().isAfter(LocalDateTime.parse("9997-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) ) {
            query = new Query();
            query.addCriteria(Criteria.where("_id").is(washEvent.get_id()));
            Update update = new Update();
            update.set("wt", wt);
            mongoTemplate.updateFirst(query, update, "wash_event");
        }
    }

    @Override
    public List<Document> eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isDepartmentGroup, Long userTypeId, Long userId, LionPage lionPage) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        if (Objects.nonNull(isDepartmentGroup) && Objects.equals(isDepartmentGroup,true)) {
            group = BasicDBObjectUtil.put(group,"$group","_id","$pdn"); //部门分组
        }else if (Objects.nonNull(isDepartmentGroup) && Objects.equals(isDepartmentGroup,false)){
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
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(userTypeId)){
            match = BasicDBObjectUtil.put(match,"$match","py",new BasicDBObject("$eq", userTypeId) );
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

        if (Objects.nonNull(userTypeId)){
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
    public Document eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime, Long regionId) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$a"); //全院
        group = BasicDBObjectUtil.put(group,"$group","allCount",new BasicDBObject("$sum",1));//全部
        group = BasicDBObjectUtil.put(group,"$group","allNoAlarm",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",false})})).append("then",1).append("else",0))));//合规

        BasicDBObject match = new BasicDBObject();
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(regionId)){
            match = BasicDBObjectUtil.put(match,"$match","ri",regionId );
        }
        BasicDBObject project = new BasicDBObject();
        project = BasicDBObjectUtil.put(project,"$project","_id",1);
        project = BasicDBObjectUtil.put(project,"$project","allNoAlarmRatio",new BasicDBObject("$divide",new String[]{"$allNoAlarm","$allCount"}));

        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(project);

        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("wash_event").aggregate(pipeline);
        AtomicReference<Document> document = new AtomicReference<>();
        aggregateIterable.forEach(d -> {
            document.set(d);
        });
        return document.get();
    }

    @Override
    public List<WashEvent> userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria.and("pi").is(userId);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("adt").gte(startDateTime) ,Criteria.where("adt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("adt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("adt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        List<WashEvent> items = mongoTemplate.find(query, WashEvent.class);
        return items;
    }

    @Override
    public IPageResultData<List<WashEvent>> userWashConformanceRatioScreen(String userName, List<Long> departmentIds, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            criteria.and("pdi").in(departmentIds);
        }
        if (Objects.nonNull(userIds) && userIds.size()>0) {
            criteria.and("pi").in(userIds);
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
        List<WashEvent> items = mongoTemplate.find(query, WashEvent.class);
        return new PageResultData(items,lionPage,0L);
    }

    @Override
    public Integer userWashConformanceRatioScreenPercentage(String userName, List<Long> departmentIds, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$a"); //全院
        group = BasicDBObjectUtil.put(group,"$group","allCount",new BasicDBObject("$sum",1));//全部
        LocalDateTime wt = LocalDateTime.parse("9997-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        group = BasicDBObjectUtil.put(group,"$group","allNoAlarm",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ia",false})})).append("then",1).append("else",0))));//合规

        BasicDBObject match = new BasicDBObject();
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        }else if (Objects.nonNull(startDateTime) && Objects.isNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$gte",startDateTime));
        }else if (Objects.isNull(startDateTime) && Objects.nonNull(endDateTime)) {
            match = BasicDBObjectUtil.put(match,"$match","adt", new BasicDBObject("$lte",endDateTime));
        }
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            match = BasicDBObjectUtil.put(match,"$match","pdi",new BasicDBObject("$in",departmentIds) );
        }
        if (Objects.nonNull(userIds) && userIds.size()>0) {
            match = BasicDBObjectUtil.put(match,"$match","pi",new BasicDBObject("$in",departmentIds) );
        }

        BasicDBObject project = new BasicDBObject();
        project = BasicDBObjectUtil.put(project,"$project","_id",1);
        project = BasicDBObjectUtil.put(project,"$project","allNoAlarmRatio",new BasicDBObject("$divide",new String[]{"$allNoAlarm","$allCount"}));

        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(project);


        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("wash_event").aggregate(pipeline);
        List<Document> list = new ArrayList<>();
        AtomicReference<Integer> allNoAlarmRatio = new AtomicReference<>(100);
        aggregateIterable.forEach(document -> {
            if (document.containsKey("allNoAlarmRatio")) {
                if (NumberUtil.isDouble(String.valueOf(document.get("allNoAlarmRatio")))) {
                    Double d = document.getDouble("allNoAlarmRatio");
                    allNoAlarmRatio.set(new BigDecimal(d).multiply(new BigDecimal(100)).intValue());
                }
            }
        });
        return allNoAlarmRatio.get();
    }
}
