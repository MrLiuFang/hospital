package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.person.TempLeave;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午9:47
 */
@Data
@ApiModel
public class ListTempLeaveVo extends TempLeave {

    @ApiModelProperty(value = "性别")
    private Gender gender;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    @ApiModelProperty(value = "疾病")
    private String disease;

    @ApiModelProperty(value = "病历号")
    private String medicalRecordNo;

    @ApiModelProperty(value = "标签码")
    private String tagCode;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "患者名字")
    private String patientName;

    @ApiModelProperty(value = "患者头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "患者头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "登记人姓名")
    private String userName;

    @ApiModelProperty(value = "登记人头像（文件id）")
    private Long userHeadPortrait;

    @ApiModelProperty(value = "登记人头像")
    private String userHeadPortraitUrl;
}
