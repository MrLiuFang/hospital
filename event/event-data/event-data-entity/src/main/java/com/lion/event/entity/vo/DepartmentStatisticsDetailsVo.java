package com.lion.event.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 下午2:47
 */
@Data
@Schema
public class DepartmentStatisticsDetailsVo {

//    @Schema(description = "科室id")
//    private Long departmentId;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "低电量设备")
    private Integer lowPowerDeviceCount = 0;

    @Schema(description = "低电量标签")
    private Integer lowPowerTagCount = 0;

    @Schema(description = "今日收到警告数量")
    private Integer alarmCount = 0;

    @Schema(description = "未处理警告数量")
    private Integer unalarmCount = 0;

    @Schema(description = "今日警告数量")
    private Integer allAlarmCount = 0;

    @Schema(description = "当前在线员工")
    private Integer onlineStaffCount = 0;

    @Schema(description = "当前监控的资产")
    private Integer assetsCount = 0;

    @Schema(description = "温湿标签")
    private Integer tagCount = 0;

    @Schema(description = "组网设备")
    private Integer cctvCount = 0;

    @Schema(description = "组网设备异常")
    private Integer cctvAlarmCount = 0;

    @Schema(description = "患者数量")
    private Integer patientCount= 0;

    @Schema(description = "患者异常数量")
    private Integer patientAlarmCount= 0;

    @Schema(description = "故障申报")
    private Integer faultCount= 0;
}
