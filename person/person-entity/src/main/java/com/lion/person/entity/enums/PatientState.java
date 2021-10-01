package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/1 上午9:32
 */
public enum PatientState implements IEnum {

    TEMPORARY_DEPARTURE(1, "临时离开"),
    ABNORMAL_DEPARTURE(2, "失联/擅自离开");

    private final int key;

    private final String desc;

    private PatientState(int key, String desc) {
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
    public static PatientState instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static PatientState instance(Integer key){
        for(PatientState item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static PatientState instance(String name){
        for(PatientState item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class PatientStateConverter extends EnumConverter<PatientState,Integer> {
    }
}
