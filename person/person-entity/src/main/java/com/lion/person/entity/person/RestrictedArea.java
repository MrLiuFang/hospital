package com.lion.person.entity.person;

import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.PersonType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午4:06
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_restricted_area",indexes = {@Index(columnList = "type"),@Index(columnList = "name"),@Index(columnList = "id_no"),@Index(columnList = "phone_number")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "限制区域")
public class RestrictedArea extends BaseEntity {

    @ApiModelProperty(value = "人员类型")
    @Convert(converter = PersonType.PersonTypeConverter.class)
    @NotNull(message = "人员类型不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "type")
    private PersonType type;

    @ApiModelProperty(value = "人员id")
    @NotNull(message = "人员id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "person_id")
    private Long personId;


}
