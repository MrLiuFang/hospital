package com.lion.event.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.event.dao.TagRecordDaoEx;
import com.lion.event.entity.TagRecord;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import lombok.extern.java.Log;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午11:22
 */
@Log
public class TagRecordDaoImpl implements TagRecordDaoEx {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public TagRecord find(Long tagId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(tagId)) {
            criteria.and("ti").is(tagId);
        }
        LocalDateTime now = LocalDateTime.now();
        criteria.andOperator( Criteria.where("ddt").gte(now.minusDays(30)) ,Criteria.where("ddt").lte(now));
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(0,1,Sort.by(Sort.Order.desc("ddt")));
        query.with(pageRequest);
        List<TagRecord> items = mongoTemplate.find(query,TagRecord.class);
        if (Objects.nonNull(items) && items.size()>0){
            return items.get(0);
        }
        return null;
//        LocalDateTime now = LocalDateTime.now();
//        BasicDBObject where = new BasicDBObject();
//        where.append("ddt",new BasicDBObject("$gte",now.minusDays(30)).append("$lte",now)).append("ti",tagId);
//        FindIterable<Document> findIterable = mongoTemplate.getCollection("tag_record").find(where,Document.class);
//        findIterable.limit(1).skip(0).sort(new BasicDBObject("ddt",-1));
//        AtomicReference<TagRecord> record = new AtomicReference<>();
//        findIterable.forEach(document -> {
//            TagRecord tagRecord = new TagRecord();
//            if (document.containsKey("_id") && Objects.nonNull(document.get("_id"))){
//                tagRecord.set_id(document.getObjectId("_id").toString());
//            }
//            if (document.containsKey("bfi") && Objects.nonNull(document.get("bfi"))) {
//                tagRecord.setBfi(document.getLong("bfi"));
//            }
//            if (document.containsKey("bui") && Objects.nonNull(document.get("bui"))) {
//                tagRecord.setBui(document.getLong("bui"));
//            }
//            if (document.containsKey("bfn") && Objects.nonNull(document.get("bfn"))) {
//                tagRecord.setBfn(document.getString("bfn"));
//            }
//            if (document.containsKey("bun") && Objects.nonNull(document.get("bun"))) {
//                tagRecord.setBun(document.getString("bun"));
//            }
//            if (document.containsKey("ddt") && Objects.nonNull(document.get("ddt"))) {
//                tagRecord.setDdt(LocalDateTime.ofInstant(document.getDate("ddt").toInstant(), ZoneId.systemDefault()));
//            }
//            if (document.containsKey("di") && Objects.nonNull(document.get("di"))) {
//                tagRecord.setDi(document.getLong("di"));
//            }
//            if (document.containsKey("dn") && Objects.nonNull(document.get("dn"))) {
//                tagRecord.setDn(document.getString("dn"));
//            }
//            if (document.containsKey("h") && Objects.nonNull(document.get("h"))) {
//                tagRecord.setH(new BigDecimal(document.getString("h")));
//            }
//            if (document.containsKey("ri") && Objects.nonNull(document.get("ri"))) {
//                tagRecord.setRi(document.getLong("ri"));
//            }
//            if (document.containsKey("rn") && Objects.nonNull(document.get("rn"))) {
//                tagRecord.setRn(document.getString("rn"));
//            }
//            if (document.containsKey("sdt") && Objects.nonNull(document.get("sdt"))) {
//                tagRecord.setSdt(LocalDateTime.ofInstant(document.getDate("sdt").toInstant(), ZoneId.systemDefault()));
//            }
//            if (document.containsKey("t") && Objects.nonNull(document.get("t"))) {
//                tagRecord.setT(new BigDecimal(document.getString("t")));
//            }
//            if (document.containsKey("ti") && Objects.nonNull(document.get("ti"))) {
//                tagRecord.setTi(document.getLong("ti"));
//            }
//            if (document.containsKey("typ") && Objects.nonNull(document.get("typ"))) {
//                tagRecord.setTyp(document.getInteger("typ"));
//            }
//            record.set(tagRecord);
//        });
//        return record.get();
    }
}
