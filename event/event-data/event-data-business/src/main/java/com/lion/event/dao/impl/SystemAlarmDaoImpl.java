package com.lion.event.dao.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.event.dao.SystemAlarmDaoEx;
import com.lion.event.entity.SystemAlarm;
import org.checkerframework.checker.units.qual.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Objects;
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

    private SystemAlarm find(String uuid){
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        return systemAlarm;
    }
}
