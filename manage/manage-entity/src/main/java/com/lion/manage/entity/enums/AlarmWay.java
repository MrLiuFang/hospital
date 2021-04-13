package com.lion.manage.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 警报方式
 * @date 2021/4/13上午10:38
 */
public enum AlarmWay implements IEnum {

    SHINE(0, "页面警报标志闪耀"),
    SOUND(1, "警报声鸣响");
    private final int key;

    private final String desc;

    private AlarmWay(int key, String desc) {
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

    @Override
    public Map<String, Object> jsonValue() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        map.put("desc", desc);
        map.put("name", getName());
        return map;
    }

    @JsonCreator
    public static AlarmWay instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AlarmWay instance(Integer key){
        for(AlarmWay item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AlarmWay instance(String name){
        for(AlarmWay item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class AlarmWayConverter extends EnumConverter<AlarmWay,Integer> {
    }
}
