package com.lion.common.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 下午5:10
 **/
public enum UnalarmType implements IEnum {

    LEAVE_REGION(10, "离开区域"),
    WASH(11, "按规定洗手"),
    NO_WASH_RULE(12, "没有警告规则"),
    DEFAULT(99, "默认值(只为填充数据)");

    private final int key;

    private final String desc;

    private UnalarmType(int key, String desc) {
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
    public static UnalarmType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static UnalarmType instance(Integer key){
        for(UnalarmType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static UnalarmType instance(String name){
        for(UnalarmType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }
}
