package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午5:39
 **/
public enum Type implements IEnum {

    ASSET_OR_DEVICE(0, "设备(资产等等)"),
    STAFF(1, "员工"),
    TEMPERATURE(2, "温度仪"),
    ERU(3, "eru"),
    HUMIDITY(4, "湿度仪"),
    TEMP_TAG(5, "临时标签"),
    PATIENT(6, "患者"),
    MIGRANT(7, "流动人员");

    private final int key;

    private final String desc;

    private Type(int key, String desc) {
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
        return getKey();
    }

    @JsonCreator
    public static Type instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static Type instance(Integer key){
        for(Type item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static Type instance(String name){
        for(Type item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }
}
