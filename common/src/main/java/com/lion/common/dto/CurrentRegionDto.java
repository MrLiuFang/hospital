package com.lion.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午4:00
 **/
@Data
@Schema
public class CurrentRegionDto implements Serializable {

    private static final long serialVersionUID = -5394114715986856960L;

    @Schema(description = "当前所在的区域Id")
    private Long regionId;

    @Schema(description = "第一次进入时间")
    private LocalDateTime firstEntryTime;

    @Schema(description = "设备产生的时间")
    private LocalDateTime time;

    @Schema(description = "系统接收到的时间")
    private LocalDateTime systemDateTime;
}
