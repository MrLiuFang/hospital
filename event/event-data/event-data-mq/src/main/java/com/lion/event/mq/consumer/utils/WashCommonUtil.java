package com.lion.event.mq.consumer.utils;

import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.event.entity.WashRecord;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 下午6:02
 **/
@Component
public class WashCommonUtil {
    @Autowired
    private RedisUtil redisUtil;

    public WashRecord mapToBean(Map<String,Object> map) {
        WashRecord washRecord = new WashRecord();
        washRecord.setPi(Objects.nonNull(map.get("pi"))?Long.valueOf(String.valueOf(map.get("pi"))):null);
        washRecord.setDdt(LocalDateTime.parse(String.valueOf(map.get("ddt")), DateTimeFormatter.ofPattern(DateTimeFormatterUtil.pattern(String.valueOf(map.get("ddt"))))));
        washRecord.setSdt(LocalDateTime.parse(String.valueOf(map.get("sdt")), DateTimeFormatter.ofPattern(DateTimeFormatterUtil.pattern(String.valueOf(map.get("sdt"))))));
        washRecord.setRi(Objects.nonNull(map.get("ri"))?Long.valueOf(String.valueOf(map.get("ri"))):null);
        washRecord.setDvi(Objects.nonNull(map.get("dvi"))?Long.valueOf(String.valueOf(map.get("dvi"))):null);
        washRecord.setUi(Objects.nonNull(map.get("uuid"))?String.valueOf(map.get("dvi")):null);

        Device device = redisUtil.getDevice(washRecord.getDvi());
        if (Objects.nonNull(device)){
            washRecord.setDvc(device.getCode());
            washRecord.setDvn(device.getName());
        }

        Region region = redisUtil.getRegionById(washRecord.getRi());
        if (Objects.nonNull(region)) {
            washRecord.setRn(region.getName());
            Build build = redisUtil.getBuild(region.getBuildId());
            if (Objects.nonNull(build)) {
                washRecord.setBui(build.getId());
                washRecord.setBun(build.getName());
            }
            BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)) {
                washRecord.setBfi(buildFloor.getId());
                washRecord.setBfn(buildFloor.getName());
            }
            Department department = redisUtil.getDepartment(region.getDepartmentId());
            if (Objects.nonNull(department)) {
                washRecord.setDi(department.getId());
                washRecord.setDn(department.getName());
            }
        }

        Department department = redisUtil.getDepartment(washRecord.getPi());
        if (Objects.nonNull(department)) {
            washRecord.setPdi(department.getId());
            washRecord.setPdn(department.getName());
        }

        User user = redisUtil.getUserById(washRecord.getPi());
        if (Objects.nonNull(user)){
            washRecord.setPy(user.getUserType().getKey());
        }

        return washRecord;
    }
}

