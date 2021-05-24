package com.lion.event.dao.impl;

import com.lion.common.enums.Type;
import com.lion.event.dao.PositionDaoEx;
import com.lion.event.entity.Position;
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
}
