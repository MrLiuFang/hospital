package com.lion.manage.entity.ward;

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
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:30
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_ward" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "病房基本信息")
public class Ward extends BaseEntity {

    @ApiModelProperty(value = "病房名称")
    @Column(name = "name")
    @NotBlank(message = "病房名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "科室")
    @Column(name = "department_id")
    @NotNull(message = "科室不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    public Long departmentId;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;

}
