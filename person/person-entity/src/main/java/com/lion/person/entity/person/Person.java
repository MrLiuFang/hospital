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

    @ApiModelProperty(value = "证件类型")
    @Column(name = "identity_document_type")
    @Convert(converter = IdentityDocumentType.IdentityDocumentTypeConverter.class)
    @NotNull(message = "证件类型不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private IdentityDocumentType identityDocumentType;

    @ApiModelProperty(value = "证件号码")
    @Column(name = "id_no")
    @NotBlank(message = "证件号码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String idNo;

    @ApiModelProperty(value = "联系电话")
    @Column(name = "phone_number")
    @NotBlank(message = "联系电话不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String phoneNumber;

    @ApiModelProperty(value = "紧急联络人")
    @Column(name = "emergency_contact")
    @NotBlank(message = "紧急联络人不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联络人电话")
    @Column(name = "emergency_contact_phone_number")
    @NotBlank(message = "紧急联络人电话不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String emergencyContactPhoneNumber;

    @ApiModelProperty(value = "标签码")
    @Column(name = "emergency_contact_phone_number")
    @NotBlank(message = "标签码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String tagCode;
}
