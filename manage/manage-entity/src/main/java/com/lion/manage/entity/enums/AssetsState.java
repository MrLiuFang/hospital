package com.lion.manage.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 资产使用状态
 * @date 2021/4/6下午2:47
 */
public enum AssetsState implements IEnum {
    NOT_USED(0, "空闲"),
    USEING(1, "使用中"),
    REPAIR(2, "维修"),
    LOSE(3, "失联");
    private final int key;

    private final String desc;

    private AssetsState(int key, String desc) {
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
    public static AssetsState instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static AssetsState instance(Integer key){
        for(AssetsState item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static AssetsState instance(String name){
        for(AssetsState item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class AssetsStateConverter extends EnumConverter<AssetsState,Integer> {
    }
}
