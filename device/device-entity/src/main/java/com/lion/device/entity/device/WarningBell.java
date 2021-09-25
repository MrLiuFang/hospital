package com.lion.device.entity.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午10:58
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_warning_well" )

@DynamicInsert
@Data
@ApiModel(description = "警示铃")
public class WarningBell extends BaseEntity {

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{2000111}",groups = {Validator.Insert.class,Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "编号")
    @NotBlank(message = "{2000112}",groups = {Validator.Insert.class,Validator.Update.class})
    private String code;

    @ApiModelProperty(value = "设备id")
    @NotBlank(message = "{2000113}",groups = {Validator.Insert.class,Validator.Update.class})
    private String warningBellId;

    @ApiModelProperty(value = "科室")
    @NotNull(message = "{0000009}",groups = {Validator.Insert.class,Validator.Update.class})
    private Long departmentId;

    @ApiModelProperty(value = "图片")
    private Long img;
}
