package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.Device;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:35
 */
@Data
@Schema
public class DetailsDeviceVo extends Device {

    @Schema(description = "归属科室名称")
    private String departmentName;

    @Schema(description = "归属科室id")
    private Long departmentId;

    @Schema(description = "建筑名称(安装位置)")
    private String buildName;

    @Schema(description = "楼层名称(安装位置)")
    private String buildFloorName;

    @Schema(description = "地图")
    private String mapUrl;

    @Schema(description = "图片")
    private String imgUrl;

}
