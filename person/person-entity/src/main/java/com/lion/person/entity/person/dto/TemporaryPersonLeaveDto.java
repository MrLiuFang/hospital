package com.lion.person.entity.person.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午4:17
 */
@Data
@Schema
public class TemporaryPersonLeaveDto {

    @Schema(description = "流动人员id")
    @NotNull(message = "{1000015}")
    private Long temporaryPersonId;

    @Schema(description = "登出原因")
    private String leaveRemarks;
}
