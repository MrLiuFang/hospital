package com.lion.person.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

public enum LogType implements IEnum {

    IN_REGION(0, "进入区域"),
    ADD_REPORT(1, "添加汇报"),
    UPDATE_HEAD_PORTRAIT(2, "修改头像"),
    UPDATE_GENDER(3, "修改性别"),
    UPDATE_PHONE_NUMBER(4, "修改联系电话"),
    UPDATE_EMERGENCY_CONTACT(5, "修改紧急联络人"),
    UPDATE_TAG_CODE(6, "修改标签码"),
    UPDATE_ADDRESS(7, "修改地址"),
    UPDATE_BIRTHDAY(8, "修改出生日期"),
    UPDATE_MEDICAL_RECORD_NO(9, "修改病历号"),
    UPDATE_DISEASE(10, "修改疾病"),
    UPDATE_DEPARTMENT(11, "修改所属科室"),
    UPDATE_WARD(12, "修改病房信息"),
    UPDATE_LEVEL(13, "修改级别"),
    UPDATE_ACTION_MODE(14, "修改行动限制信息"),
    UPDATE_TIME_QUANTUM(15, "修改可通行时间段"),
    UPDATE_REMARKS(16, "修改备注"),
    UPDATE_CARD_NUMBER(17, "修改金卡"),
    ADD(18, "添加患者"),
    UPDATE_BIND_PATIENT(19, "修改绑定患者"),
    DELETE_REPORT(20, "删除汇报"),
    ADD_TEMP_LEAVE(21, "新增临时离开权限"),
    UPDATE_NAME(22, "修改患者名称"),
    UPDATE(23, "修改患者");
    private final int key;

    private final String desc;

    private LogType(int key, String desc) {
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
    public static LogType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static LogType instance(Integer key){
        for(LogType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static LogType instance(String name){
        for(LogType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class LogTypeConverter extends EnumConverter<LogType,Integer> {
    }
}
