package com.lion.person.entity.person;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/14 下午8:35
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient_doctor",indexes = {@Index(columnList = "patient_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "患者")
public class PatientDoctor extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 786096266217805032L;
    @ApiModelProperty(value = "患者ID")
    @Column(name = "patient_id")
    @NotNull(message = "患者不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long patientId;

    @ApiModelProperty(value = "负责医生")
    @Column(name = "doctor_id")
    @NotNull(message = "负责医生不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long doctorId;
}
