package com.lion.person.entity.person;

import com.lion.core.persistence.Validator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午3:38
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_temporary_person",indexes = {@Index(columnList = "type"),@Index(columnList = "name"),@Index(columnList = "id_no"),@Index(columnList = "phone_number")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "临时人员")
public class TemporaryPerson extends Person {

    @ApiModelProperty(value = "住址")
    @Column(name = "address")
    private String address;

    @ApiModelProperty(value = "拜访人ID")
    @NotNull(message = "拜访人不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "visit_Id")
    private Long visitId;

    @ApiModelProperty(value = "拜访原因")
    @Column(name = "remarks")
    private String remarks;

}
