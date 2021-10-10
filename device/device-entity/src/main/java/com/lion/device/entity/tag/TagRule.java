package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagRuleEffect;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午10:12
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_rule" ,indexes = {@Index(columnList = "name")})

@DynamicInsert
@Data
@Schema(description = "标签规则")
public class TagRule extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -4762444619380889778L;
    @Schema(description = "规则名称")
    @Column(name = "name")
    private String name;

    @Schema(description = "绿色按钮")
    @Column(name = "green_button")
    private TagRuleEffect greenButton;

    @Schema(description = "红色按钮")
    @Column(name = "red_button")
    private TagRuleEffect redButton;

    @Schema(description = "黄色按钮")
    @Column(name = "yellow_button")
    private TagRuleEffect yellowButton;

    @Schema(description = "下方按钮")
    @Column(name = "bottom_button")
    private TagRuleEffect bottomButton;

    @Schema(description = "绿色按钮是否提示")
    @Column(name = "green_button_tip")
    private Boolean greenButtonTip = false;

    @Schema(description = "红色按钮是否提示")
    @Column(name = "red_button_tip")
    private Boolean redButtonTip = false;

    @Schema(description = "黄色按钮是否提示")
    @Column(name = "yellow_button_tip")
    private Boolean yellowButtonTip = false;

    @Schema(description = "下方按钮是否提示")
    @Column(name = "bottom_button_tip")
    private Boolean bottomButtonTip = false;

}
