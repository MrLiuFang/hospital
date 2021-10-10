package com.lion.person.entity.person.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午9:20
 */
@Data
@Schema
public class AdvanceOverTempLeaveDto {

    @Schema(description = "患者id")
    @NotNull(message = "{1000026}")
    @Size(min = 1,message = "{1000026}")
    private List< Long> patientIds;
}
