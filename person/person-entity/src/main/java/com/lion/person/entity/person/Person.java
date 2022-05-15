package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午2:57
 */
@MappedSuperclass
@Data
public abstract class Person extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -9197610700850011610L;
    @Schema(description = "头像（文件id）")
    @Column(name = "head_portrait")
    private Long headPortrait;

    @Schema(description = "性别")
    @Column(name = "gender")
    @Convert(converter = Gender.GenderConverter.class)
//    @NotNull(message = "{1000010}", groups = {Validator.Insert.class, Validator.Update.class})
    private Gender gender;

    @Schema(description = "姓名")
    @Column(name = "name")
//    @NotBlank(message = "{1000011}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "联系电话")
    @Column(name = "phone_number")
//    @NotBlank(message = "{1000012}", groups = {Validator.Insert.class, Validator.Update.class})
    private String phoneNumber;

    @Schema(description = "紧急联络人")
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Schema(description = "紧急联络人电话")
    @Column(name = "emergency_contact_phone_number")
    private String emergencyContactPhoneNumber;

    @Schema(description = "标签码")
    @Column(name = "tag_code")
//    @NotBlank(message = "{1000013}", groups = {Validator.Insert.class, Validator.Update.class})
    private String tagCode;

    @Schema(description = "住址")
    @Column(name = "address")
    private String address;

    @Schema(description = "是否登出")
    @Column(name = "is_leave")
    private Boolean isLeave =false;

    @Schema(description = "是否等待登出(通过回收箱登出)")
    @Column(name = "is_wait_leave")
    private Boolean isWaitLeave =false;

    @Schema(description = "登出时间（离开时间）")
    @Column(name = "leave_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveDateTime;

    @Schema(description = "登出原因")
    @Column(name = "leave_emarks")
    private String leaveRemarks;

    @Schema(description = "可通行时间段 [[\"09:00\",\"12:00\"],[\"13:00\",\"16:00\"]] json数据格式,时间范围不能乱,否则会影响警告")
    @Column(name = "time_quantum")
    private String timeQuantum;

    @Schema(description = "设备状态")
    @Column(name = "device_state")
    @Convert(converter = State.StateConverter.class)
    private State deviceState = State.NORMAL;

    @Schema(description = "最后的设备数据时间")
    @Column(name = "last_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastDataTime;

    
}
