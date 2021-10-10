package com.lion.person.entity.person.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午8:30
 */
@Data
@Schema
public class TransferDto {

    @Schema(description = "患者id")
    @NotNull(message = "{1000026}")
    private Long patientId;

    @Schema(description = "转移新科室Id")
    @NotNull(message = "{1000029}")
    private Long departmentId;
}
