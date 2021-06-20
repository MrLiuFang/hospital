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
 * @Description:
 * @date 2021/4/13上午11:26
 */
public enum AlarmClassify implements IEnum {

    PATIENT(0, "患者"),
    BABY(1, "婴儿"),
    STAFF(2, "职员"),
    ASSETS(3, "资产"),
    DEVICE(4, "设备"),
    TEMPERATURE_HUMIDITY_INSTRUMENT(5, "温湿"),
    POSTDOCS(6, "流动人员");
    private final int key;

    private final String desc;

    private AlarmClassify(int key, String desc) {
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
    public static AlarmClassify instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AlarmClassify instance(Integer key){
        for(AlarmClassify item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AlarmClassify instance(String name){
        for(AlarmClassify item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class AlarmClassifyConverter extends EnumConverter<AlarmClassify,Integer> {
    }
}
