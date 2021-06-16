package com.lion.person.entity.person;

import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.enums.TransferState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午5:11
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_patient",indexes = {@Index(columnList = "patient_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "患者")
public class PatientTransfer extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6410357470049605092L;
    @ApiModelProperty(value = "患者ID")
    @Column(name = "patient_id")
    private Long patientId;

    @ApiModelProperty(value = "转移前科室")
    @Column(name = "old_department_id")
    private Long oldDepartmentId;

    @ApiModelProperty(value = "转移新科室")
    @Column(name = "new_department_id")
    private Long newDepartmentId;

    @ApiModelProperty(value = "转移前床位")
    @Column(name = "old_sickbed_id")
    private Long oldSickbedId;

    @ApiModelProperty(value = "转移新床位")
    @Column(name = "new_sickbed_id")
    private Long newSickbedId;

    @ApiModelProperty(value = "接收员工")
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @ApiModelProperty(value = "接收时间")
    @Column(name = "receive_date_time")
    private LocalDateTime receiveDateTime;

    @ApiModelProperty(value = "转移状态")
    @Convert(converter = TransferState.TransferStateConverter.class)
    @Column(name = "state")
    private TransferState state;

    @ApiModelProperty(value = "离开时间（转移时间)")
    @Column(name = "leave_date_time")
    private LocalDateTime leaveDateTime;

    @ApiModelProperty(value = "转移触发时间")
    @Column(name = "trigger_date_time")
    private LocalDateTime triggerDateTime;

}
