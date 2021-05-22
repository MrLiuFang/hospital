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

    @ApiModelProperty(value = "科室id")
    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private Long departmentName;

    @ApiModelProperty(value = "低电量设备")
    private Integer lowPowerDeviceCount;

    @ApiModelProperty(value = "低电量标签")
    private Integer lowPowerTagCount;

    @ApiModelProperty(value = "今日收到警告数量")
    private Integer alarmCount;

    @ApiModelProperty(value = "未处理警告数量")
    private Integer unalarmCount;

    @ApiModelProperty(value = "今日警告数量")
    private Integer allAlarmCount;

    @ApiModelProperty(value = "当前在线员工")
    private Integer onlineStaffCount;

    @ApiModelProperty(value = "当前监控的资产")
    private Integer assetsCount;

    @ApiModelProperty(value = "温湿标签")
    private Integer tagCount;

    @ApiModelProperty(value = "组网设备")
    private Integer cctvCount;

    @ApiModelProperty(value = "组网设备异常")
    private Integer cctvAlarmCount;

    @ApiModelProperty(value = "患者数量")
    private Integer patientCount;

    @ApiModelProperty(value = "患者异常数量")
    private Integer patientAlarmCount;

    @ApiModelProperty(value = "故障申报")
    private Integer faultCount;
}
