package com.lion.device.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 设备分类
 * @date 2021/3/31上午11:00
 */
public enum DeviceType implements IEnum {
    TIME_STAR(0, "time star"),
    STANDARD_STAR(1, "standard star"),
    WATER(2, "清水"),
    DISINFECTANT_GEL(3, "免洗消毒凝膠"),
    LIQUID_SOAP(4, "洗手液"),
    ALCOHOL(5, "酒精"),
    WASHING_FOAM(6, "洗手泡沫");

    private final int key;

    private final String desc;

    private DeviceType(int key, String desc) {
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
    public static DeviceType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static DeviceType instance(Integer key){
        for(DeviceType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static DeviceType instance(String name){
        for(DeviceType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class DeviceTypeConverter extends EnumConverter<DeviceType,Integer> {
    }
}
