package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:39
 **/
public enum SystemAlarmType implements IEnum {

    JRQYQWXS(0, "进入区域前未洗手"),
    JRQYQWZGDDSBXS(1, "进入区域前未在规定的设备洗手"),
    JRQYHWXS(2, "进入区域后未洗手"),
    JRQYHWZGDDSBXS(3, "进入区域后未在规定的设备洗手"),
    DSWXS(4, "定时未洗手"),
    DSWZGDDXSSBLXXS(5, "定时未在规定的洗手设备类型洗手"),
    JRJQ(6, "进入禁区"),
    WSQCCSSQY(7, "未授权超出所属区域"),
    WDGD(8, "温度过低"),
    WDGG(9, "温度过高"),
    SDGD(10, "湿度过低"),
    SDGG(11, "湿度过高");

    private final int key;

    private final String desc;

    private SystemAlarmType(int key, String desc) {
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
    public static SystemAlarmType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static SystemAlarmType instance(Integer key){
        for(SystemAlarmType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static SystemAlarmType instance(String name){
        for(SystemAlarmType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

}
