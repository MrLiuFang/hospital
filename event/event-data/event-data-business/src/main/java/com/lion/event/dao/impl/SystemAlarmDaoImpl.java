package com.lion.event.dao.impl;

import com.lion.event.dao.SystemAlarmDaoEx;
import com.lion.event.entity.SystemAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:22
 **/
public class SystemAlarmDaoImpl implements SystemAlarmDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updateSdt(String uuid) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("ui").is(uuid);
        query.addCriteria(criteria);
        SystemAlarm systemAlarm = mongoTemplate.findOne(query, SystemAlarm.class);
        if (Objects.nonNull(systemAlarm)) {
            query = new Query();
            query.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
            Update update = new Update();
            update.set("sdt", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, "system_alarm");
        }
    }
}
