package com.lion.person.entity.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.Gender;
import com.lion.person.entity.enums.IdentityDocumentType;
import com.lion.person.entity.enums.PersonType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午2:57
 */
@MappedSuperclass
@Data
public abstract class Person extends BaseEntity {

    @ApiModelProperty(value = "头像（文件id）")
    @Column(name = "head_portrait")
    private Long headPortrait;

    @ApiModelProperty(value = "性别")
    @Column(name = "gender")
    @Convert(converter = Gender.GenderConverter.class)
    @NotNull(message = "性别不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Gender gender;

    @ApiModelProperty(value = "姓名")
    @Column(name = "name")
    @NotBlank(message = "姓名不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "联系电话")
    @Column(name = "phone_number")
    @NotBlank(message = "联系电话不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String phoneNumber;

    @ApiModelProperty(value = "紧急联络人")
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联络人电话")
    @Column(name = "emergency_contact_phone_number")
    private String emergencyContactPhoneNumber;

    @ApiModelProperty(value = "标签码")
    @Column(name = "tag_code")
    @NotBlank(message = "标签码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String tagCode;

    @ApiModelProperty(value = "住址")
    @Column(name = "address")
    private String address;

    @ApiModelProperty(value = "是否登出")
    @Column(name = "is_leave")
    private Boolean isLeave =false;

    @ApiModelProperty(value = "登出原因")
    @Column(name = "leave_emarks")
    private String leaveRemarks;
}
