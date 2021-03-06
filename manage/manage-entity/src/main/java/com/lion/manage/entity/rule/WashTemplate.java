package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:28
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_wash_template",indexes = {@Index(columnList = "name")})

@DynamicInsert
@Data
@Schema(description = "洗手规则模板")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"updateDateTime", "createUserId", "updateUserId"}
)
public class WashTemplate extends BaseEntity {

    private static final long serialVersionUID = 6103915473684030413L;
    @Schema(description = "洗手规则模板名称")
    @Column(name = "name")
    @NotBlank(message = "{2000102}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "false=通用模板，true=区域自定义模板（自定义模板在通用模板中不显示）")
    private Boolean isCustom=false;

    @Schema(description = "是否启用(true=启用，false=停用)")
    private Boolean state = true;
}
