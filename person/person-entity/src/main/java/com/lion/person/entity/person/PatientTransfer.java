package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.enums.TransferState;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Table(name = "t_patient_transfer",indexes = {@Index(columnList = "patient_id")})

@DynamicInsert
@Data
@Schema(description = "患者转移")
public class PatientTransfer extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6410357470049605092L;
    @Schema(description = "患者ID")
    @Column(name = "patient_id")
    private Long patientId;

    @Schema(description = "转移前科室")
    @Column(name = "old_department_id")
    private Long oldDepartmentId;

    @Schema(description = "转移新科室")
    @Column(name = "new_department_id")
    private Long newDepartmentId;

    @Schema(description = "转移前床位")
    @Column(name = "old_sickbed_id")
    private Long oldSickbedId;

    @Schema(description = "转移新床位")
    @Column(name = "new_sickbed_id")
    private Long newSickbedId;

    @Schema(description = "接收/取消员工")
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @Schema(description = "接收/取消时间")
    @Column(name = "receive_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveDateTime;

    @Schema(description = "转移状态")
    @Convert(converter = TransferState.TransferStateConverter.class)
    @Column(name = "state")
    private TransferState state;

    @Schema(description = "离开时间（转移时间)")
    @Column(name = "leave_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveDateTime;

    @Schema(description = "转移触发时间")
    @Column(name = "trigger_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime triggerDateTime;

}
