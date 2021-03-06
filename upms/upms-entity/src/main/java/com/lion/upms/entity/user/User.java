package com.lion.upms.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.upms.entity.enums.Gender;
import com.lion.upms.entity.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description: 用户表
 * @date 2021/3/22下午2:48
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_user"
        ,indexes = {@Index(columnList = "name"),@Index(columnList = "username"),@Index(columnList = "email"),@Index(columnList = "create_date_time"),@Index(columnList = "user_type_id") })

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"password","createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "用户")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2414603096785043264L;
    @Schema(description = "用户登陆账号")
    @Column(name = "username")
//    @NotBlank(message = "用户登陆账号不能为空", groups = {Validator.Insert.class})
//    @Length(min = 3, max = 30, message = "账号为{min}-{max}个字符", groups = {Validator.Insert.class})
//    @Pattern(regexp = "[A-Za-z0-9\\-]{3,30}", message = "账号只能是3-30个(英文/数字)字符", groups = {Validator.Insert.class})
    private String username;

    @Setter(AccessLevel.PUBLIC)
    @Schema(description = "密码（md5密码)")
    @Column(name = "password")
//    @NotBlank(message = "密码不能为空", groups = {Validator.Insert.class})
//    @Pattern(regexp = "[a-zA-Z0-9]{32}", message = "请输入正确的密码(32的MD5密文)", groups = {Validator.Insert.class})
    private String password;

    @Schema(description = "姓名")
    @Column(name = "name")
    @NotBlank(message = "{0000001}", groups = {Validator.Insert.class, Validator.Update.class})
//    @Length(min = 0, max = 30, message = "姓名不能超过{max}个字符", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "邮箱")
    @Column(name = "email")
    @Email(message = "{0000002}", groups = {Validator.Insert.class, Validator.Update.class})
    @NotBlank(message = "{0000003}", groups = {Validator.Insert.class,Validator.Update.class})
    private String email;

    @Schema(description = "头像（文件id）")
    @Column(name = "head_portrait")
    private Long headPortrait;

    @Schema(description = "性别")
    @Column(name = "gender")
    @Convert(converter = Gender.GenderConverter.class)
    @NotNull(message = "{0000004}", groups = {Validator.Insert.class, Validator.Update.class})
    private Gender gender;

    @Schema(description = "出生日期")
    @Column(name = "birthday")
    @Past(message = "{0000005}", groups = {Validator.Insert.class, Validator.Update.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "员工类型")
    @Column(name = "user_type_id")
//    @Convert(converter = UserType.UserTypeConverter.class)
//    @NotNull(message = "{0000006}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long userTypeId;

    @Schema(description = "员工编号")
    @Column(name = "number")
    @NotNull(message = "{0000007}", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer number;

    @Schema(description = "标签编码")
    @Column(name = "tag_code")
    private String tagCode;

    @Schema(description = "联系电话")
    @Column(name = "phone_number")
    @NotBlank(message = "{0000008}", groups = {Validator.Insert.class,Validator.Update.class})
    private String phoneNumber;

    @Schema(description = "住址")
    @Column(name = "address")
    private String address;

    @Schema(hidden = true,description = "账号是否未过期")
    @JsonIgnore
    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired = false;

    @Schema(hidden = true,description = "账号是否未锁定")
    @JsonIgnore
    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked = false;

    @Schema(hidden = true,description = "账号凭证是否未过期")
    @JsonIgnore
    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired = false;

    @Schema(hidden = true,description = "账号是否可用")
//    @JsonIgnore
    @Column(name = "is_nabled")
    private Boolean isEnabled = true;

    @Schema(description = "设备状态")
    @Column(name = "device_state")
    @Convert(converter = State.StateConverter.class)
    private State deviceState = State.NORMAL;

    @Schema(description = "最后的设备数据时间")
    @Column(name = "last_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastDataTime;


}
