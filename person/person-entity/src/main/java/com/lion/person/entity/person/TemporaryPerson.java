package com.lion.person.entity.person;

import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.ActionMode;
import com.lion.person.entity.enums.IdentityDocumentType;
import io.swagger.v3.oas.annotations.media.Schema;

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

@DynamicInsert
@Data
@Schema(description = "临时人员")
public class TemporaryPerson extends Person implements Serializable {

    private static final long serialVersionUID = -5294943964036491904L;
    @Schema(description = "证件类型")
    @Column(name = "identity_document_type")
    @Convert(converter = IdentityDocumentType.IdentityDocumentTypeConverter.class)
//    @NotNull(message = "{1000017}", groups = {Validator.Insert.class, Validator.Update.class})
    private IdentityDocumentType identityDocumentType;

    @Schema(description = "证件号码")
    @Column(name = "id_no")
//    @NotBlank(message = "{1000018}", groups = {Validator.Insert.class, Validator.Update.class})
    private String idNo;

    @Schema(description = "拜访人ID")
//    @NotNull(message = "{1000019}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "patient_Id")
    private Long patientId;

    @Schema(description = "科室ID（来源于拜访人）")
    @Column(name = "department_id")
    private Long departmentId;

    @Schema(description = "拜访原因")
    @Column(name = "remarks")
    private String remarks;

//    @Schema(description = "通行级别")
//    @NotNull(message = "{1000050}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Integer trafficLevel;

    @Schema(description = "行动限制")
    @Column(name = "action_mode")
    @Convert(converter = ActionMode.ActionModeConverter.class)
    private ActionMode actionMode;

}
