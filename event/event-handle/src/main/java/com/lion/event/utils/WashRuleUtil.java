package com.lion.event.utils;

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
}



