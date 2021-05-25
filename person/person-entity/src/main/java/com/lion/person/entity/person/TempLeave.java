package com.lion.person.entity.person;

import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.core.service.BaseService;
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
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:13
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_temp_leave",indexes = {@Index(columnList = "patient_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "限制区域")
public class TempLeave extends BaseEntity {

    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "patient_id")
    private Long patientId;

    @ApiModelProperty(value = "登记人id")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "离开开始时间")
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @ApiModelProperty(value = "离开结束时间")
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ApiModelProperty(value = "是否结束临时权限放开")
    @Column(name = "is_closure")
    private Boolean isClosure = false;

    @ApiModelProperty(value = "离开原因")
    @Column(name = "remarks")
    private String remarks;
}
