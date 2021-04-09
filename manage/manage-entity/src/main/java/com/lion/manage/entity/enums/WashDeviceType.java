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
 * @Description:
 * @date 2021/4/9下午4:39
 */
public enum WashDeviceType implements IEnum {

    DISINFECTION_GEL(0, "免洗消毒凝胶"),
    LIQUID_SOAP(1, "洗手液"),
    ALCOHOL(2, "酒精"),
    WASHING_FOAM(3, "洗手泡沫"),
    WATER(4, "清水");
    private final int key;

    private final String desc;

    private WashDeviceType(int key, String desc) {
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
    public static WashDeviceType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static WashDeviceType instance(Integer key){
        for(WashDeviceType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static WashDeviceType instance(String name){
        for(WashDeviceType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class WashDeviceTypeConverter extends EnumConverter<WashDeviceType,Integer> {
    }
}
