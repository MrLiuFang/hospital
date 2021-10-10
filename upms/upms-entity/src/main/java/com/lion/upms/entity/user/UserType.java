package com.lion.upms.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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

@DynamicInsert
@Data
@Schema(description = "用户类型")
public class UserType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7341159678436570822L;

    @Schema(description = "名称")
    @NotBlank(message = "{0000022}", groups = {Validator.Insert.class, Validator.Update.class})
    private String userTypeName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "病房洗手规则")
    private Boolean wardRoomWashRule;

    @Schema(description = "定时洗手规则")
    private Boolean loopWashRule;

    @Schema(description = "定时洗手规则")
    private Boolean other;
}
