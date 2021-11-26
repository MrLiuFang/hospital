package com.lion.event.dao.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.common.enums.Type;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.event.dao.CurrentPositionDaoEx;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import lombok.extern.java.Log;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午11:04
 */
@Log
public class CurrentPositionDaoImpl implements CurrentPositionDaoEx {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        match = BasicDBObjectUtil.put(match,"$match","bfi",new BasicDBObject("$eq",buildFloorId) );
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id",new BasicDBObject[]{new BasicDBObject("type","$typ"),new BasicDBObject("regionId","$ri")});
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("current_position").aggregate(pipeline);
//        [{"$match": {"bfi": {"$eq": 852498362870530048}}}, {"$group": {"_id": [{"type": "$typ"}, {"regionId": "$ri"}], "count": {"$sum": 1}}}]
        List<RegionStatisticsDetails> list = new ArrayList<RegionStatisticsDetails>();
        aggregateIterable.forEach(document -> {
            if (document.containsKey("_id")) {
                List<Document> _id = document.getList("_id",Document.class);
                Long regionId = null;
                Type type = null;
                Integer count =NumberUtil.isInteger(String.valueOf(document.get("count")))?document.getInteger("count"):0;
                for (Document d :_id){
                    if (d.containsKey("type")) {
                        type = Type.instance(d.getInteger("type"));
                    }
                    if (d.containsKey("regionId")) {
                        regionId = d.getLong("regionId");
                    }
                };
                if (Objects.nonNull(regionId) && Objects.nonNull(type) && Objects.nonNull(count)) {
                    if (map.containsKey(regionId)){
                        RegionStatisticsDetails regionStatisticsDetails = map.get(regionId);
                        if (Objects.equals(type,Type.HUMIDITY) || Objects.equals(type,Type.TEMPERATURE)) {
                            regionStatisticsDetails.setTagCount((Objects.nonNull(regionStatisticsDetails.getTagCount())?regionStatisticsDetails.getTagCount():0)+count);
                        }else if (Objects.equals(type,Type.STAFF) ){
                            regionStatisticsDetails.setStaffCount(count);
                        }
//                        else if (Objects.equals(type,Type.ASSET) ){
//                            regionStatisticsDetails.setAssetsCount(count);
//                        }
                        else if (Objects.equals(type,Type.PATIENT) ){
                            regionStatisticsDetails.setPatientCount(count);
                        }else if (Objects.equals(type,Type.MIGRANT) ){
                            regionStatisticsDetails.setMigrantCount(count);
                        }
                    }
                }
            }
        });
        return map;
    }



}
