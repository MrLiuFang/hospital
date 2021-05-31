package com.lion.upms.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/31 上午10:26
 */
public enum State implements IEnum {

    NORMAL(1, "正常"),
    ALARM(2, "警报"),
    OFFLINE(3,"离线");

    private final int key;

    private final String desc;

    private State(int key, String desc) {
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
        return getName();
    }

    @JsonCreator
    public static State instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static State instance(Integer key){
        for(State item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static State instance(String name){
        for(State item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class StateConverter extends EnumConverter<State,Integer> {
    }
}
