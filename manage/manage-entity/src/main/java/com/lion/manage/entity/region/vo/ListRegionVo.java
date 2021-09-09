package com.lion.manage.entity.region.vo;

import com.lion.core.persistence.Validator;
import com.lion.manage.entity.region.Region;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/14 上午11:43
 **/
@Data
@ApiModel
public class ListRegionVo extends Region {

    @ApiModelProperty(value = "建筑")
    private String buildName;

    @ApiModelProperty(value = "建筑楼层")
    private String buildFloorName;

    @ApiModelProperty(value = "科室")
    private String departmentName;

}
