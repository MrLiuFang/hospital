package com.lion.manage.entity.region;

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

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午8:29
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_region_type" )

@DynamicInsert
@Data
@ApiModel(description = "区域类型")
public class RegionType extends BaseEntity {

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{2000105}", groups = {Validator.Insert.class, Validator.Update.class})
    private String regionTypeName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
