package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.enums.LogType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:24
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient_log",indexes = {@Index(columnList = "patient_id")})

@DynamicInsert
@Data
@Schema(description = "患者日志")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class PatientLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -744298217698937325L;
    @Schema(description = "患者ID")
    @Column(name = "patient_id")
    @NotNull(message = "{1000005}",groups = {Validator.Update.class, Validator.Insert.class})
    private Long patientId;

    @Schema(description = "日志类型")
    @Convert(converter = LogType.LogTypeConverter.class)
    private LogType logType;

    @Schema(description = "操作人ID")
    private Long operationUserId;

    @Schema(description = "内容")
    @Column(name = "content")
    @NotBlank(message = "{1000007}",groups = {Validator.Update.class, Validator.Insert.class})
    private String content;
}
