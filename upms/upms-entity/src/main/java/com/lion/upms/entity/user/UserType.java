package com.lion.upms.entity.user;

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
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:47
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_user_type")
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "用户类型")
public class UserType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7341159678436570822L;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{0000022}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "病房洗手规则")
    private Boolean wardRoomWashRule;

    @ApiModelProperty(value = "定时洗手规则")
    private Boolean loopWashRule;

    @ApiModelProperty(value = "定时洗手规则")
    private Boolean other;
}
