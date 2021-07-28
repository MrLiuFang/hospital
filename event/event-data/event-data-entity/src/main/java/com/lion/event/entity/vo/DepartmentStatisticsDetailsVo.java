package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 下午2:47
 */
@Data
@ApiModel
public class DepartmentStatisticsDetailsVo {

//    @ApiModelProperty(value = "科室id")
//    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "低电量设备")
    private Integer lowPowerDeviceCount = 0;

    @ApiModelProperty(value = "低电量标签")
    private Integer lowPowerTagCount = 0;

    @ApiModelProperty(value = "今日收到警告数量")
    private Integer alarmCount = 0;

    @ApiModelProperty(value = "未处理警告数量")
    private Integer unalarmCount = 0;

    @ApiModelProperty(value = "今日警告数量")
    private Integer allAlarmCount = 0;

    @ApiModelProperty(value = "当前在线员工")
    private Integer onlineStaffCount = 0;

    @ApiModelProperty(value = "当前监控的资产")
    private Integer assetsCount = 0;

    @ApiModelProperty(value = "温湿标签")
    private Integer tagCount = 0;

    @ApiModelProperty(value = "组网设备")
    private Integer cctvCount = 0;

    @ApiModelProperty(value = "组网设备异常")
    private Integer cctvAlarmCount = 0;

    @ApiModelProperty(value = "患者数量")
    private Integer patientCount= 0;

    @ApiModelProperty(value = "患者异常数量")
    private Integer patientAlarmCount= 0;

    @ApiModelProperty(value = "故障申报")
    private Integer faultCount= 0;
}
