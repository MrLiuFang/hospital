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
 * @Description: 警报声持续时间
 * @date 2021/4/13上午10:40
 */
public enum AlarmDuration implements IEnum {

    ONE(0, "短响一次"),
    THREE(1, "短响三次"),
    CONTINUED(2, "持续长响");
    private final int key;

    private final String desc;

    private AlarmDuration(int key, String desc) {
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
    public static AlarmDuration instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AlarmDuration instance(Integer key){
        for(AlarmDuration item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AlarmDuration instance(String name){
        for(AlarmDuration item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class AlarmDurationConverter extends EnumConverter<AlarmDuration,Integer> {
    }
}
