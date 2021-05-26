package com.lion.person.entity.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午9:20
 */
@Data
@ApiModel
public class AdvanceOverTempLeaveDto {
    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    private Long patientId;
}
