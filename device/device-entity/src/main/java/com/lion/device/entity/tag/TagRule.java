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
import java.io.Serializable;

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
public class TagRule extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -4762444619380889778L;
    @ApiModelProperty(value = "规则名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "绿色按钮")
    @Column(name = "green_button")
    private TagRuleEffect greenButton;

    @ApiModelProperty(value = "红色按钮")
    @Column(name = "red_button")
    private TagRuleEffect redButton;

    @ApiModelProperty(value = "黄色按钮")
    @Column(name = "yellow_button")
    private TagRuleEffect yellowButton;

    @ApiModelProperty(value = "下方按钮")
    @Column(name = "bottom_button")
    private TagRuleEffect bottomButton;

    @ApiModelProperty(value = "绿色按钮是否提示")
    @Column(name = "green_button_tip")
    private Boolean greenButtonTip = false;

    @ApiModelProperty(value = "红色按钮是否提示")
    @Column(name = "red_button_tip")
    private Boolean redButtonTip = false;

    @ApiModelProperty(value = "黄色按钮是否提示")
    @Column(name = "yellow_button_tip")
    private Boolean yellowButtonTip = false;

    @ApiModelProperty(value = "下方按钮是否提示")
    @Column(name = "bottom_button_tip")
    private Boolean bottomButtonTip = false;

}
