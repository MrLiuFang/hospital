package com.lion.event.mq.consumer.utils;

import com.lion.common.dto.HumitureRecordDto;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.tag.Tag;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午9:10
 **/
@Component
public class TagCommonUtil {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设置区域位置相关信息
     * @param humitureRecordDto
     * @return
     */
    public HumitureRecordDto setRegionInfo(HumitureRecordDto humitureRecordDto){
        Region region = redisUtil.getRegionById(humitureRecordDto.getRi());
        if (Objects.nonNull(region)) {
            humitureRecordDto.setRn(region.getName());
            Build build = redisUtil.getBuild(region.getBuildId());
            if (Objects.nonNull(build)) {
                humitureRecordDto.setBui(build.getId());
                humitureRecordDto.setBun(build.getName());
            }
            BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)) {
                humitureRecordDto.setBfi(buildFloor.getId());
                humitureRecordDto.setBfn(buildFloor.getName());
            }
            Department department = redisUtil.getDepartment(region.getDepartmentId());
            if (Objects.nonNull(department)) {
                humitureRecordDto.setDi(department.getId());
                humitureRecordDto.setDn(department.getName());
            }
        }
        if (Objects.nonNull(humitureRecordDto.getTi())) {
            Tag tag = redisUtil.getTagById(humitureRecordDto.getTi()) ;
            if (Objects.nonNull(tag)){
                humitureRecordDto.setSdi(tag.getDepartmentId());
            }
        }
        return humitureRecordDto;
    }
}
