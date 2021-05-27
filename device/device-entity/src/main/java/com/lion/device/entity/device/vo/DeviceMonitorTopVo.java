package com.lion.device.entity.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 上午9:58
 */
@Data
@ApiModel
public class DeviceMonitorTopVo {

    @ApiModelProperty(value = "正常设备")
    private Integer normalCount = 0;

    @ApiModelProperty(value = "离线设备")
    private Integer offlineCount =0 ;

    @ApiModelProperty(value = "故障设备")
    private Integer faultCount = 0;
}
