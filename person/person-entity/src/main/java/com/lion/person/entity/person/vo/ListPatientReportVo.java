package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.PatientReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:58
 */
@Data
@ApiModel
public class ListPatientReportVo extends PatientReport {

    @ApiModelProperty(value = "汇报员工姓名")
    private String reportUserName;

    @ApiModelProperty(value = "汇报员工头像")
    private Long reportUserHeadPortrait;

    @ApiModelProperty(value = "汇报员工头像")
    private String reportUserHeadPortraitUrl;
}
