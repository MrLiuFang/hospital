package com.lion.person.entity.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午8:30
 */
@Data
@ApiModel
public class TransferDto {

    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    private Long patientId;

    @ApiModelProperty(value = "转移新科室Id")
    @NotNull(message = "转移新科室不能为空")
    private Long departmentId;
}
