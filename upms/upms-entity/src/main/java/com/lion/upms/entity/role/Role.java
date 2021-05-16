package com.lion.upms.entity.role;

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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Mr.Liu
 * @Description: 角色
 * @date 2021/3/22下午3:22
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_role")
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "角色表")
public class Role extends BaseEntity {

    @ApiModelProperty(value = "角色名称")
    @Column(name = "name")
    @NotBlank(message = "角色名称不能为空",groups = {Validator.Update.class, Validator.Insert.class})
    @Size(message = "角色名称为{min}-{max}个字符",max = 30,min = 3,groups = {Validator.Update.class, Validator.Insert.class})
    @Pattern(regexp = "^[\\u4E00-\\u9FA5\\w+]+$",message = "角色名称不能包含特殊字符",groups = {Validator.Update.class, Validator.Insert.class})
    private String name;

    @ApiModelProperty(value = "角色编码")
    @Column(name = "code")
    @NotBlank(message = "角色编码不能为空",groups = {Validator.Update.class, Validator.Insert.class})
    @Size(message = "角色编码为{min}-{max}个字符",max = 30,min = 3,groups = {Validator.Update.class, Validator.Insert.class})
    @Pattern(regexp = "^\\w+$",message = "角色编码只能是数字、英文字母或者下划线组成的字符串",groups = {Validator.Update.class, Validator.Insert.class})
    private String code;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty(value = "权限")
    @Column(name = "resources",length = 2000)
    private String resources;

    @ApiModelProperty(value = "是否默认角色（0：否，1：是）默认角色不能删除")
    @Column(name = "is_default" )
    private Boolean isDefault =false;
}
