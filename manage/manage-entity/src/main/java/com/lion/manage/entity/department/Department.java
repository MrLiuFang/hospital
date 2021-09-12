package com.lion.manage.entity.department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
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
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description: 科室
 * @date 2021/3/23下午2:04
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_department"
        ,indexes = {@Index(columnList = "name"),@Index(columnList = "parent_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "科室")
public class Department extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3294625261945415578L;
    @ApiModelProperty(value = "科室名称")
    @Column(name = "name")
    @NotBlank(message = "{2000025}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty(value = "父节点Id")
    @Column(name = "parent_id")
    private Long parentId=0L;
}
