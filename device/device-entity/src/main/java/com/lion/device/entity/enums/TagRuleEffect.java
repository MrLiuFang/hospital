package com.lion.device.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午10:17
 **/
public enum TagRuleEffect implements IEnum {
    ALARM_KNOW(0, "警报知悉"),
    EMPLOYEE_CALL(1, "员工呼叫"),
    RESET(2, "复位"),
    CANCEL(3, "取消呼叫");

    private final int key;

    private final String desc;

    private TagRuleEffect(int key, String desc) {
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
    public static TagRuleEffect instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static TagRuleEffect instance(Integer key){
        for(TagRuleEffect item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static TagRuleEffect instance(String name){
        for(TagRuleEffect item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class TagRuleEffectConverter extends EnumConverter<TagRuleEffect,Integer> {
    }
}
