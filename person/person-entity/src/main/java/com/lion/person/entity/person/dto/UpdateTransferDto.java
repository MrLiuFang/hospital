package com.lion.person.entity.person.dto;

import com.lion.person.entity.enums.TransferState;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/19 上午9:48
 */
@Data
@Schema
public class UpdateTransferDto {
    @Schema(description = "患者id")
    @NotNull(message = "{1000026}")
    private Long patientId;

    @Schema(description = "状态")
    @NotNull(message = "{1000030}")
    private TransferState transferState;


}
