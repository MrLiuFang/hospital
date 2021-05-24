package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:19
 */
public enum  IdentityDocumentType implements IEnum {

    ID_CARD(0, "身份证"),
    PASSPORT(1, "护照");

    private final int key;

    private final String desc;

    private IdentityDocumentType(int key, String desc) {
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
    public static IdentityDocumentType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static IdentityDocumentType instance(Integer key){
        for(IdentityDocumentType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static IdentityDocumentType instance(String name){
        for(IdentityDocumentType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class IdentityDocumentTypeConverter extends EnumConverter<IdentityDocumentType,Integer> {
    }
}
