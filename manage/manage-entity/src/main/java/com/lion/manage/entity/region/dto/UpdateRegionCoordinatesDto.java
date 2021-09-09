package com.lion.manage.entity.region.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.region.Region;
import lombok.Data;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午12:46
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"buildFloorId","buildId","deviceGroupId","remarks","departmentId","type","isPublic","name","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateRegionCoordinatesDto extends Region {
}
