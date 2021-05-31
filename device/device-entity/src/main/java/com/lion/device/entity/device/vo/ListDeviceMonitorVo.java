package com.lion.device.entity.device.vo;

import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 上午10:59
 */
@Data
@ApiModel
public class ListDeviceMonitorVo {

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备图片")
    private Long img;

    @ApiModelProperty(value = "设备图片")
    private String imgUrl;

    @ApiModelProperty(value = "设备类型")
    private DeviceClassify classify;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "设备编码")
    private String code;

    @ApiModelProperty(value = "建筑id(安装位置)")
    private String buildName;

    @ApiModelProperty(value = "楼层id(安装位置)")
    private String buildFloorName;

    @ApiModelProperty(value = "状态")
    private State state;

    @ApiModelProperty(value = "电量")
    private Integer battery;

}
