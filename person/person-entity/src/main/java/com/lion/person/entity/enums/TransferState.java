package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午5:14
 */
public enum TransferState implements IEnum {

    PENDING_TRANSFER(0, "待转移"),
    TRANSFERRING(1, "转移中"),
    WAITING_TO_RECEIVE(2, "待接收"),
    CANCEL(3, "取消"),
    FINISH(4, "完成");

    private final int key;

    private final String desc;

    private TransferState(int key, String desc) {
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
    public static TransferState instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static TransferState instance(Integer key){
        for(TransferState item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static TransferState instance(String name){
        for(TransferState item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class TransferStateConverter extends EnumConverter<TransferState,Integer> {
    }
}
