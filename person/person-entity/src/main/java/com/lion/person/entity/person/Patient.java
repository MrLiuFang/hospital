package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.LionObjectMapper;
import com.lion.core.persistence.Validator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:36
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient",indexes = {@Index(columnList = "name"),@Index(columnList = "phone_number")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "患者")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"updateDateTime", "createUserId", "updateUserId"}
)
public class Patient extends Person {

    @ApiModelProperty(value = "出生日期")
    @Column(name = "birthday")
    @Past(message = "出生日期不能大于/等于当前日期", groups = {Validator.Insert.class, Validator.Update.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "出生日期不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private LocalDate birthday;

    @ApiModelProperty(value = "病历号")
    @Column(name = "medical_record_no")
    @NotBlank(message = "病历号不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String medicalRecordNo;

    @ApiModelProperty(value = "科室ID")
    @Column(name = "department_id")
    private Long departmentId;

    @ApiModelProperty(value = "病房(房间)ID")
    @Column(name = "room_id")
    private Long roomId;

    @ApiModelProperty(value = "病房ID")
    @Column(name = "ward_id")
    private Long wardId;

    @ApiModelProperty(value = "病床ID")
    @Column(name = "sickbed_id")
    @NotNull(message = "病床ID不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long sickbedId;

    @ApiModelProperty(value = "疾病")
    @Column(name = "disease")
    @NotBlank(message = "疾病不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String disease;

    @ApiModelProperty(value = "患者级别(1/2/3)级")
    @Column(name = "level")
    @NotNull(message = "患者级别不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer level;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    @Length(max = 255,message = "最多255个字符",groups = {Validator.Insert.class, Validator.Update.class})
    private String remarks;

    @ApiModelProperty(value = "绑定患者")
    @Column(name = "bind_patient_id")
    private Long bindPatientId;
}
