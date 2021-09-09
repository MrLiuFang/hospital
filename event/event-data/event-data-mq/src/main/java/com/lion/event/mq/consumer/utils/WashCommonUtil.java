package com.lion.event.mq.consumer.utils;

import com.lion.common.dto.WashRecordDto;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.expose.user.UserTypeExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/16 下午6:02
 **/
@Component
public class WashCommonUtil {
    @Autowired
    private RedisUtil redisUtil;

    @DubboReference
    private UserTypeExposeService userTypeExposeService;

    public WashRecordDto setInfo(WashRecordDto washRecordDto){
        Device device = redisUtil.getDevice(washRecordDto.getDvi());
        if (Objects.nonNull(device)){
            washRecordDto.setDvc(device.getCode());
            washRecordDto.setDvn(device.getName());
        }

        Region region = redisUtil.getRegionById(washRecordDto.getRi());
        if (Objects.nonNull(region)) {
            washRecordDto.setRn(region.getName());
            Build build = redisUtil.getBuild(region.getBuildId());
            if (Objects.nonNull(build)) {
                washRecordDto.setBui(build.getId());
                washRecordDto.setBun(build.getName());
            }
            BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)) {
                washRecordDto.setBfi(buildFloor.getId());
                washRecordDto.setBfn(buildFloor.getName());
            }
            Department department = redisUtil.getDepartment(region.getDepartmentId());
            if (Objects.nonNull(department)) {
                washRecordDto.setDi(department.getId());
                washRecordDto.setDn(department.getName());
            }
        }

        Department department = redisUtil.getDepartmentByUserId(washRecordDto.getPi());
        if (Objects.nonNull(department)) {
            washRecordDto.setPdi(department.getId());
            washRecordDto.setPdn(department.getName());
        }

        User user = redisUtil.getUserById(washRecordDto.getPi());
        if (Objects.nonNull(user)){
            UserType userType = userTypeExposeService.findById(user.getUserTypeId());
            if (Objects.nonNull(userType)) {
                washRecordDto.setPy(userType.getId());
            }
        }
        return washRecordDto;
    }
}

