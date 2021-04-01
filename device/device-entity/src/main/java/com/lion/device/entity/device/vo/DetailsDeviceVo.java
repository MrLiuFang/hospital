package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.Device;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:35
 */
@Data
@ApiModel
public class DetailsDeviceVo extends Device {

    @ApiModelProperty(value = "建筑名称(安装位置)")
    private String buildName;

    @ApiModelProperty(value = "楼层名称(安装位置)")
    private String buildFloorName;

    @ApiModelProperty(value = "地图")
    private String mapUrl;
}
