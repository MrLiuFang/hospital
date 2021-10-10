package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.PatientReport;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:58
 */
@Data
@Schema
public class ListPatientReportVo extends PatientReport {

    @Schema(description = "汇报员工姓名")
    private String reportUserName;

    @Schema(description = "汇报员工头像")
    private Long reportUserHeadPortrait;

    @Schema(description = "汇报员工头像")
    private String reportUserHeadPortraitUrl;
}
