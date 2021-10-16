package com.lion.manage.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/16 下午4:54
 */
public enum HavingMonitor implements IEnum {

    NOT_BINDING(0, "没有绑定区域"),
    NOT_DEFINITION(1, "病床/病房所在区域的定位设备不满足定义");
    private final int key;

    private final String desc;

    private HavingMonitor(int key, String desc) {
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
    public static HavingMonitor instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static HavingMonitor instance(Integer key){
        for(HavingMonitor item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static HavingMonitor instance(String name){
        for(HavingMonitor item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }
}
