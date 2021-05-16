package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:22
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_assets" ,indexes = {@Index(columnList = "tag_id"),@Index(columnList = "assets_id")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "标签与资产关联")
public class TagAssets extends BaseEntity {

    @ApiModelProperty(value = "标签id")
    @NotNull(message = "标签id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_id")
    private Long tagId;

    @ApiModelProperty(value = "资产id")
    @NotNull(message = "资产id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "assets_id")
    private Long assetsId;

    @ApiModelProperty(value = "绑定时间")
    @NotNull(message = "绑定时间不能为空", groups = {Validator.Insert.class})
    @Column(name = "binding_time")
    private LocalDateTime bindingTime;

    @ApiModelProperty(value = "解绑时间")
    @NotNull(message = "时间不能为空", groups = { Validator.Update.class})
    @Column(name = "unbinding_time")
    private LocalDateTime unbindingTime;
}
