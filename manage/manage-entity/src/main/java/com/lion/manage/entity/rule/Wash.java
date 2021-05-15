package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.WashRuleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:22
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_wash",indexes = {@Index(columnList = "name")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "洗手规则")
public class Wash extends BaseEntity {

    @ApiModelProperty(value = "规则名称")
    @Column(name = "name",nullable = false)
    @NotBlank(message = "规则名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "洗手规则类型")
    @Column(name = "type",nullable = false)
    @Convert(converter = WashRuleType.WashRuleTypeConverter.class)
    @NotNull(message = "洗手规则类型不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private WashRuleType type;

    @ApiModelProperty(value = "洗手间隔")
    @Column(name = "interval")
    private Integer interval;

    @ApiModelProperty(value = "洗手时长")
    @Column(name = "duration")
    private Integer duration;

    @ApiModelProperty(value = "是否提醒")
    @Column(name = "remind")
    @NotNull(message = "是否提醒不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean remind;

    @ApiModelProperty(value = "超时提醒")
    @Column(name = "overtime_remind")
    private Integer overtimeRemind;


    @ApiModelProperty(value = "进入之后X分钟需要洗手")
    @Column(name = "after_entering_time")
    private Integer afterEnteringTime;

    @ApiModelProperty(value = "进入之前X分钟需要洗手")
    @Column(name = "before_entering_time")
    private Integer beforeEnteringTime;

    @ApiModelProperty(value = "是否适用所有员工")
    @Column(name = "is_all_user")
    private Boolean isAllUser;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;
}
