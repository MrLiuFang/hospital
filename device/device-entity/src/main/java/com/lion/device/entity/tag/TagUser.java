package com.lion.device.entity.tag;

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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:46
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_user" ,indexes = {@Index(columnList = "tag_id"),@Index(columnList = "user_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "标签与员工关联")
public class TagUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7470517760288238683L;
    @ApiModelProperty(value = "标签id")
    @NotNull(message = "{3000019}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_id")
    private Long tagId;

    @ApiModelProperty(value = "员工(用户)id")
    @NotNull(message = "{3000024}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "绑定时间")
    @NotNull(message = "{4000019}", groups = {Validator.Insert.class})
    @Column(name = "binding_time",updatable = false)
    private LocalDateTime bindingTime = LocalDateTime.now();

    @ApiModelProperty(value = "解绑时间")
    @NotNull(message = "{4000020}", groups = { Validator.Update.class})
    @Column(name = "unbinding_time",insertable = false)
    private LocalDateTime unbindingTime;
}
