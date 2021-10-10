package com.lion.device.entity.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 上午9:58
 */
@Data
@Schema
public class DeviceMonitorTopVo {

    @Schema(description = "正常设备")
    private Integer normalCount = 0;

    @Schema(description = "离线设备")
    private Integer offlineCount =0 ;

    @Schema(description = "故障设备")
    private Integer faultCount = 0;
}
