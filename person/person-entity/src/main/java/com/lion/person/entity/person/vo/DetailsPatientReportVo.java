package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.PatientReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class DetailsPatientReportVo extends PatientReport {

    @Schema(description = "汇报员工姓名")
    private String reportUserName;

    @Schema(description = "汇报员工头像")
    private Long reportUserHeadPortrait;

    @Schema(description = "汇报员工头像url")
    private String reportUserHeadPortraitUrl;
}
