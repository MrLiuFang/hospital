package com.lion.event.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午8:47
 */
@Data
@Schema(description = "区域统计")
public class RegionStatisticsDetails {

    @Schema(description = "员工数量")
    private Integer staffCount;

    @Schema(description = "患者数量")
    private Integer patientCount;

    @Schema(description = "资产数量")
    private Integer assetsCount;

    @Schema(description = "温湿标签数量")
    private Integer tagCount;

    @Schema(description = "流动人员数量")
    private Integer migrantCount;

    @Schema(description = "区域id")
    private Long regionId;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "区域坐标组")
    private String coordinates;

    @Schema(description = "是否发生警告")
    private Boolean isAlarm;

    @Schema(description = "组网设备数量")
    private Integer cctvCount;

    @Schema(description = "今天员工访问数量")
    private Integer todayStaffCount;

    @Schema(description = "今天患者访问数量")
    private Integer todayPatientCount;

    @Schema(description = "今天资产访问数量")
    private Integer todayAssetsCount;

    @Schema(description = "今天流动人员访问数量")
    private Integer todayMigrantCount;
}
