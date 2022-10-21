package com.lion.event.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class UpdateWashEventStateVo {

    @Schema(description = "洗手事件id")
    private String eventId;

    @Schema(description = "事件状态(6=在指定区域无进行洗手操作,7=未使用标准的洗手设备洗手)isAlarm=true是不传此值")
    private Integer state;

    @Schema(description = "是否触发警告(false=合规/true=不合规)")
    private Boolean isAlarm;
}
