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
 * @Description: 标签类型
 * @date 2021/4/7上午9:06
 */
public enum TagType implements IEnum {

    DISPOSABLE(0, "一次性"),
    ORDINARY(1, "普通"),
    BUTTON(2, "按钮"),
    BABY(3, "婴儿"),
    STAFF(4, "职员"),
    TEMPERATURE_HUMIDITY(5, "温湿");

    private final int key;

    private final String desc;

    private TagType(int key, String desc) {
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
    public static TagType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static TagType instance(Integer key){
        for(TagType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static TagType instance(String name){
        for(TagType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class TagTypeConverter extends EnumConverter<TagType,Integer> {
    }
}
