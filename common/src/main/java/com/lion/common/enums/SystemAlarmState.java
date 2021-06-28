package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午1:37
 */
public enum SystemAlarmState implements IEnum {
    UNTREATED(0, "未处理"),
    PROCESSED(1, "已处理(熟知)操作员处理"),
    CALL(2, "主动呼叫"),
    CANCEL_CALL(3, "取消呼叫"),
    WELL_KNOWN(4, "警告熟知(员工通过按钮熟知)");

    private final int key;

    private final String desc;

    private SystemAlarmState(int key, String desc) {
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
    public static SystemAlarmState instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static SystemAlarmState instance(Integer key){
        for(SystemAlarmState item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static SystemAlarmState instance(String name){
        for(SystemAlarmState item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class SystemAlarmStateConverter extends EnumConverter<SystemAlarmState,Integer> {
    }
}
