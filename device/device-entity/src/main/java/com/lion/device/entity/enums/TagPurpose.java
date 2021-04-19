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
 * @Description:
 * @date 2021/4/7下午7:45
 */
public enum TagPurpose implements IEnum {

    PATIENT(0, "患者"),
    STAFF(1, "职员"),
    ASSETS(2, "资产"),
    POSTDOCS(3, "流动人员"),
    THERMOHYGROGRAPH(4, "温湿仪");

    private final int key;

    private final String desc;

    private TagPurpose(int key, String desc) {
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
    public static TagPurpose instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static TagPurpose instance(Integer key){
        for(TagPurpose item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static TagPurpose instance(String name){
        for(TagPurpose item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class TagPurposeConverter extends EnumConverter<TagPurpose,Integer> {
    }
}
