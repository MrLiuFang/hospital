package com.lion.person.entity.person.dto;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午3:29
 */
@Data
@Schema
public class PatientLeaveDto {

    @Schema(description = "患者id")
    @NotNull(message = "{1000026}")
    private Long patientId;

    @Schema(description = "是否登出(true=登出,false=取消登出)")
    @NotNull(message = "{1000027}")
    private Boolean isLeave;

    @Schema(description = "登出原因")
    private String leaveRemarks;

}
