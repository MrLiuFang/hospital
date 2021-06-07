package com.lion.person.entity.person.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午3:29
 */
@Data
@ApiModel
public class PatientLeaveDto {

    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    private Long patientId;

    @ApiModelProperty(value = "是否登出(true=登出,false=取消登出)")
    @NotNull(message = "是否登出不能为空")
    private Boolean isLeave;

    @ApiModelProperty(value = "登出原因")
    private String leaveRemarks;

}
