package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 下午4:05
 **/
public enum EventAlarmType implements IEnum {

    JRQRXS(0, "进入区域前未在规定时间内洗手"),
    JQQXSWZGDDXSSBXS(1, "进入区域前洗手未在规定洗手设备洗手"),
    JRHWXS(2, "进入区域后未在规定的时间内洗手"),
    JRHWZGDDSBXS(3, "进入区域前后洗手未在规定洗手设备洗手"),
    DSWXS(4, "定时未洗手");

    private final int key;

    private final String desc;

    private EventAlarmType(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getDesc(){
        return desc;
    }

//    @Override
//    public Map<String, Object> jsonValue() {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("key", key);
//        map.put("desc", desc);
//        map.put("name", getName());
//        return map;
//    }

    @Override
    public Object jsonValue() {
        return getKey();
    }

    @JsonCreator
    public static EventAlarmType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static EventAlarmType instance(Integer key){
        for(EventAlarmType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static EventAlarmType instance(String name){
        for(EventAlarmType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }
}
