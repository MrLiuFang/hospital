package com.lion.manage.entity.assets;

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
 * @createDateTime 2021/9/7 上午9:31
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets_type")

@DynamicInsert
@Data
@ApiModel(description = "资产类型")
public class AssetsType extends BaseEntity {

    private static final long serialVersionUID = -4555413920023650945L;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{2000099}", groups = {Validator.Insert.class, Validator.Update.class})
    private String assetsTypeName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
