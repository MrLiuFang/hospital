package com.lion.manage.entity.build.vo;

import com.lion.manage.entity.build.Build;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午2:25
 */
@Data
@ApiModel
public class ListBuildVo extends Build {

    @ApiModelProperty(value = "楼层数")
    private Integer totalFloors;
}
