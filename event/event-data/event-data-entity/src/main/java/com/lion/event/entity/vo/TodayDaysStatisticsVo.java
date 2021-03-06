package com.lion.event.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-18 14:12
 **/
@Data
@Builder
@Schema
public class TodayDaysStatisticsVo {

    @Schema(description = "警报总数")
    private long total;

    @Schema(description = "患者数量")
    private long patientCount;

    @Schema(description = "流动人员数量")
    private long temporaryPersonCount;

    @Schema(description = "职员数量")
    private long staffCount;

    @Schema(description = "资产数量")
    private long assetsCount;

    @Schema(description = "温湿数量")
    private long humidCount;
}
