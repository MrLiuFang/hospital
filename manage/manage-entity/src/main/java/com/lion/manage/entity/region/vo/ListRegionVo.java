package com.lion.manage.entity.region.vo;

import com.lion.core.persistence.Validator;
import com.lion.manage.entity.region.Region;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/14 上午11:43
 **/
@Data
@Schema
public class ListRegionVo extends Region {

    @Schema(description = "建筑")
    private String buildName;

    @Schema(description = "建筑楼层")
    private String buildFloorName;

    @Schema(description = "科室")
    private String departmentName;

}
