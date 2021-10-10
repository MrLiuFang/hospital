package com.lion.event.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 上午10:20
 */
@Data
@Schema
public class RegionStatisticsDetailsVo {

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
