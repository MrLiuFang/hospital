package com.lion.manage.entity.enums;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lion.core.IEnum;
import com.lion.core.common.enums.EnumConverter;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/27 下午5:39
 **/
public enum SystemAlarmType implements IEnum {

//    JRQYQWXS(0, "进入区域前未洗手(在指定区域无进行洗手操作)"),
//    JRQYQWZGDDSBXS(1, "进入区域前未在规定的设备洗手(未使用标准的洗手设备洗手)"),
//    JRQYHWXS(2, "进入区域后未洗手(在指定区域无进行洗手操作)"),
//    JRQYHWZGDDSBXS(3, "进入区域后未在规定的设备洗手(未使用标准的洗手设备洗手)"),
    DSWXS(4, "定时未洗手"),
//    DSWZGDDXSSBLXXS(5, "定时未在规定的洗手设备类型洗手(未使用标准的洗手设备洗手)"),
    ZZDQYWJXXSCZ(6, "在指定区域无进行洗手操作"),
    WXYBZDXSSBXS(7, "未使用标准的洗手设备洗手"),
    WDDBZSXSC(8, "未到达标准洗手时长"),
    CCXDFW(9, "超出行动范围"),
    WJSQQXBQ(10, "未经授权取下标签"),
    CSJWFYDJX(11, "长时间无法移动迹象"),
    ZDHJ(12, "主动呼叫"),
    JRJQ(13, "进入禁区"),
    WSQCCSSQY(14, "未授权超出所属区域"),
    WDGDG(15, "温度过低/高"),
//    WDGG(16, "温度过高"),
    SDGDG(17, "湿度过低/高"),
//    SDGG(18, "湿度过高"),
    BQDCBZ(19, "电池不足"),
    SBGZ(20, "设备故障"),
    TAG_LOSE(21, "失联");

    private final int key;

    private final String desc;

    private SystemAlarmType(int key, String desc) {
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
    public static SystemAlarmType instance(Object value){
        if (Objects.isNull(value)){
            return null;
        }
        if (NumberUtil.isInteger(String.valueOf(value))) {
            return instance(Integer.valueOf(String.valueOf(value)));
        }
        return instance(String.valueOf(value));
    }

    private static SystemAlarmType instance(Integer key){
        for(SystemAlarmType item : values()){
            if (item.getKey()==key){
                return item;
            }
        }
        return null;
    }

    private static SystemAlarmType instance(String name){
        for(SystemAlarmType item : values()){
            if(Objects.equals(item.getName(),name)){
                return item;
            }
        }
        return null;
    }

    public static class SystemAlarmTypeConverter extends EnumConverter<SystemAlarmType,Integer> {
    }
}
