package com.lion.device.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-11 10:22
 **/
public enum TagRuleLogType implements IEnum {
    ADD(0, "新增规则"),
    UPDATE(1, "修改规则"),
    ADD_USER(2, "新增员工"),
    DELETE_USER(3, "删除员工"),
    RENAME(4, "重命名"),
    DELETE(5, "删除规则");

    private final int key;

    private final String desc;

    private TagRuleLogType(int key, String desc) {
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
    public static TagRuleLogType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static TagRuleLogType instance(Integer key){
        for(TagRuleLogType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static TagRuleLogType instance(String name){
        for(TagRuleLogType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class TagRuleTypeConverter extends EnumConverter<TagRuleLogType,Integer> {
    }
}
