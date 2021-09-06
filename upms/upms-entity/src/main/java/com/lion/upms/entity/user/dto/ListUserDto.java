package com.lion.upms.entity.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午8:49
 */
@Data
@ApiModel
public class ListUserDto {

    @ApiModelProperty(value = "所属科室ID")
    private Long departmentId;

    @ApiModelProperty(value = "员工类型Id")
    private Collection<Long> userTypeIds;

    @ApiModelProperty(value = "员工编号")
    private Integer number;

    @ApiModelProperty(value = "员工姓名")
    private String name;
}
