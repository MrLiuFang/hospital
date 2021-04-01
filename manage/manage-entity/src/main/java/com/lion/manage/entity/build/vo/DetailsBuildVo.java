package com.lion.manage.entity.build.vo;

import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午1:50
 */
@Data
@ApiModel
public class DetailsBuildVo extends Build {

    @ApiModelProperty(value = "楼层")
    private List<BuildFloor> buildFloors;
}
