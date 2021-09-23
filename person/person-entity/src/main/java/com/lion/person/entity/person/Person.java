package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "头像（文件id）")
    @Column(name = "head_portrait")
    private Long headPortrait;

    @ApiModelProperty(value = "性别")
    @Column(name = "gender")
    @Convert(converter = Gender.GenderConverter.class)
//    @NotNull(message = "{1000010}", groups = {Validator.Insert.class, Validator.Update.class})
    private Gender gender;

    @ApiModelProperty(value = "姓名")
    @Column(name = "name")
//    @NotBlank(message = "{1000011}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "联系电话")
    @Column(name = "phone_number")
//    @NotBlank(message = "{1000012}", groups = {Validator.Insert.class, Validator.Update.class})
    private String phoneNumber;

    @ApiModelProperty(value = "紧急联络人")
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联络人电话")
    @Column(name = "emergency_contact_phone_number")
    private String emergencyContactPhoneNumber;

    @ApiModelProperty(value = "标签码")
    @Column(name = "tag_code")
//    @NotBlank(message = "{1000013}", groups = {Validator.Insert.class, Validator.Update.class})
    private String tagCode;

    @ApiModelProperty(value = "住址")
    @Column(name = "address")
    private String address;

    @ApiModelProperty(value = "是否登出")
    @Column(name = "is_leave")
    private Boolean isLeave =false;

    @ApiModelProperty(value = "是否等待登出(通过回收箱登出)")
    @Column(name = "is_wait_leave")
    private Boolean isWaitLeave =false;

    @ApiModelProperty(value = "登出时间（离开时间）")
    @Column(name = "leave_date_time")
    private LocalDateTime leaveDateTime;

    @ApiModelProperty(value = "登出原因")
    @Column(name = "leave_emarks")
    private String leaveRemarks;

    @ApiModelProperty(value = "可通行时间段 [[\"09:00\",\"12:00\"],[\"13:00\",\"16:00\"]] json数据格式,时间范围不能乱,否则会影响警告")
    @Column(name = "time_quantum")
    private String timeQuantum;

    @ApiModelProperty(value = "设备状态")
    @Column(name = "device_state")
    @Convert(converter = State.StateConverter.class)
    private State deviceState = State.NORMAL;

    @ApiModelProperty(value = "最后的设备数据时间")
    @Column(name = "last_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastDataTime;
}
