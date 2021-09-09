package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午9:57
 */
public enum ActionMode implements IEnum {

    NO_WALK(-1, "禁足"), LIMIT(0, "限制"),NO_LIMIT(1, "不限制"),OUTPATIENT(2, "专科门诊患者"),PATIENT_VISITORS(3, "住院病患访客"),OTHER(4, "其它");

    private final int key;

    private final String desc;

    private ActionMode(int key, String desc) {
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
    public static ActionMode instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static ActionMode instance(Integer key){
        for(ActionMode item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static ActionMode instance(String name){
        for(ActionMode item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class ActionModeConverter extends EnumConverter<ActionMode,Integer> {
    }
}
