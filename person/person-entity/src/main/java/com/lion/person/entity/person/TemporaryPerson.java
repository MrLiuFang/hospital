package com.lion.person.entity.person;

import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.IdentityDocumentType;
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

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:38
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_temporary_person",indexes = {@Index(columnList = "name"),@Index(columnList = "id_no"),@Index(columnList = "phone_number")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "临时人员")
public class TemporaryPerson extends Person implements Serializable {

    private static final long serialVersionUID = -5294943964036491904L;
    @ApiModelProperty(value = "证件类型")
    @Column(name = "identity_document_type")
    @Convert(converter = IdentityDocumentType.IdentityDocumentTypeConverter.class)
    @NotNull(message = "证件类型不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private IdentityDocumentType identityDocumentType;

    @ApiModelProperty(value = "证件号码")
    @Column(name = "id_no")
    @NotBlank(message = "证件号码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String idNo;

    @ApiModelProperty(value = "拜访人ID")
    @NotNull(message = "拜访人不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "patient_Id")
    private Long patientId;

    @ApiModelProperty(value = "科室ID（来源于拜访人）")
    @Column(name = "department_id")
    private Long departmentId;

    @ApiModelProperty(value = "拜访原因")
    @Column(name = "remarks")
    private String remarks;

}
