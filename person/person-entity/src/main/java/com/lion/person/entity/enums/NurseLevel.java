package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午9:29
 */
public enum NurseLevel implements IEnum {

    ORDINARY_FORBIDDEN(2, "普通禁足"),
    ORDINARY_PATIENT(3, "普通患者"),
    ICU_FORBIDDEN(4, "普通患者"),
    ICU_PATIENT(5, "普通患者");

    private final int key;

    private final String desc;

    private NurseLevel(int key, String desc) {
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
    public static NurseLevel instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static NurseLevel instance(Integer key){
        for(NurseLevel item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static NurseLevel instance(String name){
        for(NurseLevel item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class NurseLevelConverter extends EnumConverter<NurseLevel,Integer> {
    }
}
