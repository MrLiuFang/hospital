package com.lion.device.entity.device.vo;

import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 上午10:59
 */
@Data
@Schema
public class ListDeviceMonitorVo {

    @Schema(description = "设备id")
    private Long deviceId;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备图片")
    private Long img;

    @Schema(description = "设备图片")
    private String imgUrl;

    @Schema(description = "设备类型")
    private DeviceClassify classify;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "设备编码")
    private String code;

    @Schema(description = "建筑id(安装位置)")
    private String buildName;

    @Schema(description = "楼层id(安装位置)")
    private String buildFloorName;

    @Schema(description = "状态")
    private State state;

    @Schema(description = "是否在线")
    private Boolean isOnline =false;

    @Schema(description = "电量")
    private Integer battery;

}
