package com.lion.event.utils;

import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceType;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.Wash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午9:36
 **/
@Component
public class WashRuleUtil {

    @Autowired
    private RedisUtil redisUtil;

    public Boolean judgeDevide(Long deviceId, Wash wash){
        List<Device> washDevice = redisUtil.getWashDevice(wash.getId());
        Device device = redisUtil.getDevice(deviceId);
        for (Device obj : washDevice){
            if (Objects.equals(obj,device)) {
                return true;
            }
        };
        return false;
    }

    public Boolean judgeDevideType(Long deviceId, Wash wash){
        List<WashDeviceType> list = redisUtil.getWashDeviceType(wash.getId());
        Device device = redisUtil.getDevice(deviceId);
        if (Objects.nonNull(list) && list.size()>0 && Objects.nonNull(device)){
            for(WashDeviceType washDeviceType : list) {
                if (Objects.equals(washDeviceType,WashDeviceType.ALCOHOL)) {
                    if (Objects.equals(device.getDeviceType(),DeviceType.ALCOHOL)){
                        return true;
                    }
                }else if (Objects.equals(washDeviceType,WashDeviceType.DISINFECTION_GEL)) {
                    if (Objects.equals(device.getDeviceType(),DeviceType.DISINFECTANT_GEL)){
                        return true;
                    }
                }else if (Objects.equals(washDeviceType,WashDeviceType.LIQUID_SOAP)) {
                    if (Objects.equals(device.getDeviceType(),DeviceType.LIQUID_SOAP)){
                        return true;
                    }
                }else if (Objects.equals(washDeviceType,WashDeviceType.WASHING_FOAM)) {
                    if (Objects.equals(device.getDeviceType(),DeviceType.WASHING_FOAM)){
                        return true;
                    }
                }else if (Objects.equals(washDeviceType,WashDeviceType.WATER)) {
                    if (Objects.equals(device.getDeviceType(),DeviceType.WATER)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}



