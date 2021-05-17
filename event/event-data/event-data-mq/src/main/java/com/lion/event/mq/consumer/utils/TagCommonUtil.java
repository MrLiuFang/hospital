package com.lion.event.mq.consumer.utils;

import com.lion.common.dto.TagRecordDto;
import com.lion.common.utils.RedisUtil;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:10
 **/
@Component
public class TagCommonUtil {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设置区域位置相关信息
     * @param tagRecordDto
     * @return
     */
    public TagRecordDto setRegionInfo(TagRecordDto tagRecordDto){
        Region region = redisUtil.getRegionById(tagRecordDto.getRi());
        if (Objects.nonNull(region)) {
            tagRecordDto.setRn(region.getName());
            Build build = redisUtil.getBuild(region.getBuildId());
            if (Objects.nonNull(build)) {
                tagRecordDto.setBui(build.getId());
                tagRecordDto.setBun(build.getName());
            }
            BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)) {
                tagRecordDto.setBfi(buildFloor.getId());
                tagRecordDto.setBfn(buildFloor.getName());
            }
            Department department = redisUtil.getDepartment(region.getDepartmentId());
            if (Objects.nonNull(department)) {
                tagRecordDto.setDi(department.getId());
                tagRecordDto.setDn(department.getName());
            }
        }
        return tagRecordDto;
    }
}
