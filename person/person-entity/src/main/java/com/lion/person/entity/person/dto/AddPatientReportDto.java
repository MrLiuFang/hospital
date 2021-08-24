package com.lion.person.entity.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.PatientReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午4:00
 */
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","reportUserId","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddPatientReportDto extends PatientReport {

    @ApiModelProperty(value = "汇报员工Id")
    @NotNull(message = "{1000020}",groups = {Validator.Update.class, Validator.Insert.class})
    private Long userId;
}
