package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 性别枚举
 * @date 2021/3/22下午2:51
 */
public enum Gender implements IEnum {
    MAN(0, "男"), WOMAN(1, "女");

    private final int key;

    private final String desc;

    private Gender(int key, String desc) {
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
    public static Gender instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static Gender instance(Integer key){
        for(Gender item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static Gender instance(String name){
        for(Gender item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class GenderConverter extends EnumConverter<Gender,Integer> {
    }
}
