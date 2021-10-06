package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.LionObjectMapper;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.ActionMode;
import com.lion.person.entity.enums.NurseLevel;
import com.lion.person.entity.enums.PatientState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:36
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient",indexes = {@Index(columnList = "name"),@Index(columnList = "phone_number"),@Index(columnList = "sickbed_id")})

@DynamicInsert
@Data
@ApiModel(description = "患者")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"updateDateTime", "createUserId", "updateUserId"}
)
public class Patient extends Person implements Serializable {

    private static final long serialVersionUID = 6519257488177091836L;
    @ApiModelProperty(value = "出生日期")
    @Column(name = "birthday")
    @Past(message = "{0000005}", groups = {Validator.Insert.class, Validator.Update.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
//    @NotNull(message = "{1000000}", groups = {Validator.Insert.class, Validator.Update.class})
    private LocalDate birthday;

    @ApiModelProperty(value = "病历号")
    @Column(name = "medical_record_no")
//    @NotBlank(message = "{1000001}", groups = {Validator.Insert.class, Validator.Update.class})
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
//    @NotNull(message = "{1000002}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long sickbedId;

    @ApiModelProperty(value = "疾病")
    @Column(name = "disease")
//    @NotBlank(message = "疾病不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String disease;

    @ApiModelProperty(value = "患者级别(1/2/3)级")
    @Column(name = "level")
//    @NotNull(message = "{1000003}", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer level;

//    @ApiModelProperty(value = "护理级别")
//    @Column(name = "nurse_level")
//    @NotNull(message = "{1000048}", groups = {Validator.Insert.class, Validator.Update.class})
//    @Convert(converter = NurseLevel.NurseLevelConverter.class)
//    private NurseLevel nurseLevel;

    @ApiModelProperty(value = "行动限制")
    @Column(name = "action_mode")
    @Convert(converter = ActionMode.ActionModeConverter.class)
    private ActionMode actionMode;

    @ApiModelProperty(value = "病人状态")
    @Column(name = "patient_state")
    @Convert(converter = PatientState.PatientStateConverter.class)
    private PatientState patientState;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
//    @Length(max = 255,message = "{1000004}",groups = {Validator.Insert.class, Validator.Update.class})
    private String remarks;

    @ApiModelProperty(value = "绑定患者")
    @Column(name = "bind_patient_id")
    private Long bindPatientId;

    @ApiModelProperty(value = "金卡号")
    @Column(name = "card_number")
    private String cardNumber;
}
