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
 * @date 2021/4/7下午8:51
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_postdocs" ,indexes = {@Index(columnList = "tag_id"),@Index(columnList = "postdocs_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "标签与流动人员关联")
public class TagPostdocs extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7781310604889118075L;
    @ApiModelProperty(value = "标签id")
    @NotNull(message = "{3000019}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_id")
    private Long tagId;

    @ApiModelProperty(value = "流动人员id")
    @NotNull(message = "{3000023}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "postdocs_id")
    private Long postdocsId;

    @ApiModelProperty(value = "绑定时间")
    @NotNull(message = "{4000019}", groups = {Validator.Insert.class})
    @Column(name = "binding_time")
    private LocalDateTime bindingTime = LocalDateTime.now();

    @ApiModelProperty(value = "解绑时间")
    @NotNull(message = "{4000020}", groups = { Validator.Update.class})
    @Column(name = "unbinding_time")
    private LocalDateTime unbindingTime;
}
