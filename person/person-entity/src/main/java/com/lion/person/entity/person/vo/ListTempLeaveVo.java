package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.person.TempLeave;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class ListTempLeaveVo extends TempLeave {

    @Schema(description = "性别")
    private Gender gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "疾病")
    private String disease;

    @Schema(description = "病历号")
    private String medicalRecordNo;

    @Schema(description = "标签码")
    private String tagCode;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "患者名字")
    private String patientName;

    @Schema(description = "患者头像（文件id）")
    private Long headPortrait;

    @Schema(description = "患者头像")
    private String headPortraitUrl;

    @Schema(description = "登记人姓名")
    private String userName;

    @Schema(description = "登记人头像（文件id）")
    private Long userHeadPortrait;

    @Schema(description = "登记人头像")
    private String userHeadPortraitUrl;
}
