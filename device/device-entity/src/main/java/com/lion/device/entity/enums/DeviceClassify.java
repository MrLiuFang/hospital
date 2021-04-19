package com.lion.device.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 设备大类
 * @date 2021/3/31上午11:07
 */
public enum DeviceClassify implements IEnum {

    STAR_AP(0, "star ap"),
    MONITOR(1, "monitor"),
    VIRTUAL_WALL(2, "virtual wall"),
    LF_EXCITER(3, "lf exciter"),
    HAND_WASHING(4, "hand washing"),
    RECYCLING_BOX(5, "回收箱"),
    ;

    private final int key;

    private final String desc;

    private DeviceClassify(int key, String desc) {
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
    public static DeviceClassify instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static DeviceClassify instance(Integer key){
        for(DeviceClassify item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static DeviceClassify instance(String name){
        for(DeviceClassify item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class DeviceClassifyConverter extends EnumConverter<DeviceClassify,Integer> {
    }
}
