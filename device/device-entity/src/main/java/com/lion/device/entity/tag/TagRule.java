package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagRuleEffect;
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
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午10:12
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_rule" ,indexes = {@Index(columnList = "name")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "标签规则")
public class TagRule extends BaseEntity {


    @ApiModelProperty(value = "规则名称")
    @NotBlank(message = "规则名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "绿色按钮")
    @NotNull(message = "绿色按钮不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "green_button")
    private TagRuleEffect greenButton;

    @ApiModelProperty(value = "红色按钮")
    @NotNull(message = "红色按钮不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "red_button")
    private TagRuleEffect redButton;

    @ApiModelProperty(value = "黄色按钮")
    @NotNull(message = "黄色按钮不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "yellow_button")
    private TagRuleEffect yellowButton;

    @ApiModelProperty(value = "黑色按钮是否")
    @NotNull(message = "黑色按钮不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "black_button")
    private TagRuleEffect blackButton;

    @ApiModelProperty(value = "绿色按钮是否提示")
    @Column(name = "green_button_tip")
    private Boolean greenButtonTip = false;

    @ApiModelProperty(value = "红色按钮是否提示")
    @Column(name = "red_button_tip")
    private Boolean redButtonTip = false;

    @ApiModelProperty(value = "黄色按钮是否提示")
    @Column(name = "yellow_button_tip")
    private Boolean yellowButtonTip = false;

    @ApiModelProperty(value = "黑色按钮是否提示")
    @Column(name = "black_button_tip")
    private Boolean blackButtonTip = false;

}
