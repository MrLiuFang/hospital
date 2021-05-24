package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:11
 */
public enum PersonType implements IEnum {

    PATIENT(0, "患者"),
    TEMPORARY_PERSON(1, "临时人员");

    private final int key;

    private final String desc;

    private PersonType(int key, String desc) {
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
    public static PersonType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static PersonType instance(Integer key){
        for(PersonType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static PersonType instance(String name){
        for(PersonType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class PersonTypeConverter extends EnumConverter<PersonType,Integer> {
    }
}
