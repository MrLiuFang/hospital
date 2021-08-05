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
 * @Description: 资产分类
 * @date 2021/4/6下午2:40
 */
public enum AssetsType implements IEnum {

    AID(0, "急救设备"),
    NURSE(1, "护理设备"),
    OTHER(2, "其它"),
    PACEMAKER(3, "心脏起搏器"),
    ELECTROCARDIOGRAPH(4, "心电图"),
    DRIP_MONITOR(5, "自动滴流器"),
    SPHYGMOMANOMETER(6, "血压计");
    private final int key;

    private final String desc;

    private AssetsType(int key, String desc) {
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
    public static AssetsType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AssetsType instance(Integer key){
        for(AssetsType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AssetsType instance(String name){
        for(AssetsType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class AssetsTypeConverter extends EnumConverter<AssetsType,Integer> {
    }
}
