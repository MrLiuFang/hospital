package com.lion.person.entity.person.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema
@Data
public class TodayStatisticsVo {
    @Schema(
            description = "今日登记人数"
    )
    private int todayRegisterCount;
    @Schema(
            description = "今日患者登记人数"
    )
    private int todayPatientRegisterCount;
    @Schema(
            description = "今日流动人员登记人数"
    )
    private int todayTemporaryPersonRegisterCount;

    @Schema(
            description = "今日未登出人数"
    )
    private int notLeaveCount;
    @Schema(
            description = "今日患者未登出人数"
    )
    private int patientNotLeaveCount;
    @Schema(
            description = "今日流动人员未登出人数"
    )
    private int temporaryPersonNotLeaveCount;
}
