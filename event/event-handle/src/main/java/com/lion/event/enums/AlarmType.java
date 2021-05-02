package com.lion.event.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import sun.java2d.pipe.LoopBasedPipe;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:39
 **/
public enum AlarmType implements IEnum {

    REGION_WASH_ALARM(0, "区域洗手警告"),
    LOOP_WASH_ALARM(1, "定时洗手警告"),
    DEVICE_ALARM(2, "设备警告"),
    PATIENT_ALARM(3, "患者警告");

    private final int key;

    private final String desc;

    private AlarmType(int key, String desc) {
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
    public static AlarmType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AlarmType instance(Integer key){
        for(AlarmType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AlarmType instance(String name){
        for(AlarmType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

}
