package com.lion.event.dao.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.Type;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.event.dao.SystemAlarmDaoEx;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:22
 **/
public class SystemAlarmDaoImpl implements SystemAlarmDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void updateSdt(String uuid) {
        SystemAlarm systemAlarm = find(uuid);
        if (Objects.nonNull(systemAlarm)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
            Update update = new Update();
            update.set("sdt", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
    }

    @Override
    public void unalarm(String uuid) {
        SystemAlarm systemAlarm = find(uuid);
        if (Objects.nonNull(systemAlarm)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
            Update update = new Update();
            update.set("ua", true);
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
        redisTemplate.opsForValue().set(RedisConstants.UNALARM+uuid,true,24, TimeUnit.DAYS);
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        LocalDateTime now = LocalDateTime.now();
        match = BasicDBObjectUtil.put(match,"$match","bfi",new BasicDBObject("$eq",buildFloorId) );
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",LocalDateTime.of(now.toLocalDate(), LocalTime.MIN) ).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$ri");
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("system_alarm").aggregate(pipeline);
        List<RegionStatisticsDetails> list = new ArrayList<RegionStatisticsDetails>();
        aggregateIterable.forEach(document -> {
            if (document.containsKey("_id")) {
                Long regionId = document.getLong("_id");
                Integer count =document.getInteger("count");
                if (Objects.nonNull(regionId)  && Objects.nonNull(count) && count>0) {
                    if (map.containsKey(regionId)){
                        RegionStatisticsDetails regionStatisticsDetails = map.get(regionId);
                        regionStatisticsDetails.setIsAlarm(true);
                    }
                }
            }
        });
        return map;
    }

    public Map<String, Integer> groupCount(Long departmentId) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        LocalDateTime now = LocalDateTime.now();
        match = BasicDBObjectUtil.put(match,"$match","di",new BasicDBObject("$eq",departmentId) );
        match = BasicDBObjectUtil.put(match,"$match","dt", new BasicDBObject("$gte",LocalDateTime.of(now.toLocalDate(), LocalTime.MIN) ).append("$lte",now));
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$di");
        group = BasicDBObjectUtil.put(group,"$group","allAlarmCount",new BasicDBObject("$sum",1));
        group = BasicDBObjectUtil.put(group,"$group","unalarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ua",false})})).append("then",1).append("else",0))));
        group = BasicDBObjectUtil.put(group,"$group","alarmCount",new BasicDBObject("$sum",new BasicDBObject("$cond",new BasicDBObject("if",new BasicDBObject("$and",new BasicDBObject[]{new BasicDBObject("$eq",new Object[]{"$ua",true})})).append("then",1).append("else",0))));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("wash_event").aggregate(pipeline);
        Map<String, Integer> map = new HashMap<>();
        aggregateIterable.forEach(document -> {
            map.put("allAlarmCount",document.getInteger("allAlarmCount"));
            map.put("unalarmCount",document.getInteger("unalarmCount"));
            map.put("alarmCount",document.getInteger("alarmCount"));
        });
        return map;
    }

    private SystemAlarm find(String uuid){
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        return systemAlarm;
    }



}
