package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 员工类型
 * @date 2021/3/22下午3:04
 */
public enum UserType implements IEnum {

    DOCTOR(0, "医生"),
    NURSE(1, "护士"),
    SECURITY_STAFF(2, "保安"),
    CLEANER(3, "保卫"),
    REPAIR(4, "维修人员"),
    OTHER(5, "其它");

    private final int key;

    private final String desc;

    private UserType(int key, String desc) {
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
    public static UserType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static UserType instance(Integer key){
        for(UserType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static UserType instance(String name){
        for(UserType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }
}
