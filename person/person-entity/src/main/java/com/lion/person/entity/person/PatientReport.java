package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient_report",indexes = {@Index(columnList = "patient_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "患者医护汇报")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class PatientReport extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -5482937945633708331L;
    @ApiModelProperty(value = "患者ID")
    @Column(name = "patient_id")
    @NotNull(message = "{1000005}",groups = {Validator.Update.class, Validator.Insert.class})
    private Long patientId;

    @ApiModelProperty(value = "汇报内容")
    @Column(name = "content")
    @NotBlank(message = "{1000009}",groups = {Validator.Update.class, Validator.Insert.class})
    private String content;

    @ApiModelProperty(value = "汇报员工ID")
    @Column(name = "report_user_id")
    private Long reportUserId;
}
