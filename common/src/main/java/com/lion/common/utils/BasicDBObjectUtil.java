package com.lion.common.utils;

import com.mongodb.BasicDBObject;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/11 下午6:54
 **/
public class BasicDBObjectUtil {

    public static BasicDBObject put(BasicDBObject basicDBObject, String key,String key1,Object value){
        if (basicDBObject.containsKey(key)) {
            ((BasicDBObject)basicDBObject.get(key)).append(key1,value);
        }else {
            basicDBObject.put(key,new BasicDBObject(key1,value));
        }
        return basicDBObject;
    }
}
