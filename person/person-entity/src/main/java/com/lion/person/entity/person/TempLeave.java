package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.core.service.BaseService;
import io.swagger.v3.oas.annotations.media.Schema;

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
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:13
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_temp_leave",indexes = {@Index(columnList = "patient_id")})

@DynamicInsert
@Data
@Schema(description = "临时离开权限")
public class TempLeave extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2751099054472220914L;
    @Schema(description = "患者id")
    @NotNull(message = "{1000005}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "patient_id")
    private Long patientId;

    @Schema(description = "登记人id")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "离开开始时间")
    @Column(name = "start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @Schema(description = "离开结束时间")
    @Column(name = "end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Schema(description = "是否提前结束临时权限放开")
    @Column(name = "is_closure")
    private Boolean isClosure = false;

    @Schema(description = "离开原因")
    @Column(name = "remarks")
    private String remarks;
}
