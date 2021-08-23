package com.lion.person.entity.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午4:17
 */
@Data
@ApiModel
public class TemporaryPersonLeaveDto {

    @ApiModelProperty(value = "流动人员id")
    @NotNull(message = "{1000015}")
    private Long temporaryPersonId;

    @ApiModelProperty(value = "登出原因")
    private String leaveRemarks;
}
