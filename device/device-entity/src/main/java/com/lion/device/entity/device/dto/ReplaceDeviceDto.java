package com.lion.device.entity.device.dto;

import com.lion.core.persistence.Validator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema
public class ReplaceDeviceDto {

    @Schema(description = "被替换的id")
    private Long oldId;

    @Schema(description = "替换的id")
    private Long newId;

    @Schema(description = "设备名称")
    @NotBlank(message = "{4000003}")
    private String name;

    @Schema(description = "设备编号")
    @NotNull(message = "{4000004}")
    private String code;

    @Schema(description = "图片id")
    private Long img;

}
