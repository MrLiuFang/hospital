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
 * @date 2021/4/1上午9:22
 */
//public enum RegionType implements IEnum {
//
//    CONSULTATION_ROOM(0, "诊疗室"),
//    PHYSIOTHERAPY_ROOM(1, "理疗室"),
//    NURSE_STATION(2, "护士站"),
//    DOCTOR_OFFICE(3, "医生办公区"),
//    NURSE_LOUNGE(4, "护士休息室"),
//    WARD(5, "病房"),
//    SICKBED(6, "病床"),
//    OTHER(7, "其它");
//    private final int key;
//
//    private final String desc;
//
//    private RegionType(int key, String desc) {
//        this.key = key;
//        this.desc = desc;
//    }
//
//    @Override
//    public Integer getKey() {
//        return key;
//    }
//
//    @Override
//    public String getName() {
//        return this.toString();
//    }
//
//    @Override
//    public String getDesc(){
//        return desc;
//    }
//
////    @Override
////    public Map<String, Object> jsonValue() {
////        Map<String, Object> map = new HashMap<String, Object>();
////        map.put("key", key);
////        map.put("desc", desc);
////        map.put("name", getName());
////        return map;
////    }
//
//    @Override
//    public Object jsonValue() {
//        return getName();
//    }
//
//    @JsonCreator
//    public static RegionType instance(Object value){
//        if (Objects.isNull(value)){
//            return null;
//        }
//        if (NumberUtil.isInteger(String.valueOf(value))) {
//            return instance(Integer.valueOf(String.valueOf(value)));
//        }
//        return instance(String.valueOf(value));
//    }
//
//    private static RegionType instance(Integer key){
//        for(RegionType item : values()){
//            if (item.getKey()==key){
//                return item;
//            }
//        }
//        return null;
//    }
//
//    private static RegionType instance(String name){
//        for(RegionType item : values()){
//            if(Objects.equals(item.getName(),name)){
//                return item;
//            }
//        }
//        return null;
//    }
//
//    public static class RegionTypeConverter extends EnumConverter<RegionType,Integer> {
//    }
//}
