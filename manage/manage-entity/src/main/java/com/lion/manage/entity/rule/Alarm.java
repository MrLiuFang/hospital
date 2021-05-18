package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.AlarmDuration;
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
 * @date 2021/4/13上午10:33
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm",indexes = {@Index(columnList = "content"),@Index(columnList = "classify"),@Index(columnList = "level")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "警报规则")
public class Alarm extends BaseEntity {

    @ApiModelProperty(value = "警报分类")
    @Column(name = "classify")
    @Convert(converter = AlarmClassify.AlarmClassifyConverter.class)
    @NotNull(message = "警报分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AlarmClassify classify;

    @ApiModelProperty(value = "级别(仅限患者分类下的(1,2,3级))")
    @Column(name = "level")
    private Integer level;

    @ApiModelProperty(value = "排序")
    @Column(name = "sort")
    @NotNull(message = "排序不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer sort;

    @ApiModelProperty(value = "警报编码")
    @Column(name = "code",updatable = false)
    @Convert(converter = SystemAlarmType.SystemAlarmTypeConverter.class)
    @NotNull(message = "警报编码不能为空", groups = {Validator.Insert.class,Validator.Update.class})
    private SystemAlarmType code;

    @ApiModelProperty(value = "警报内容")
    @Column(name = "content")
    @NotBlank(message = "警报内容不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String content;

    @ApiModelProperty(value = "警报声持续时间")
    @Column(name = "duration")
    @Convert(converter = AlarmDuration.AlarmDurationConverter.class)
    @NotNull(message = "警报声持续时间不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AlarmDuration duration;

    @ApiModelProperty(value = "blueCode")
    @Column(name = "blue_code")
    private String blueCode;

    @ApiModelProperty(value = "再次提醒未处理警报事件")
    @Column(name = "again")
    @NotNull(message = "再次提醒未处理警报事件不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean again = false;

    @ApiModelProperty(value = "提醒间隔时间")
    @Column(name = "Interval")
    private Integer Interval;

    @ApiModelProperty(value = "是否发邮件给科室负责人")
    @Column(name = "is_send_mail_to_department_manager")
    @NotNull(message = "是否发邮件给科室负责人不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean isSendMailToDepartmentManager = false;

    @ApiModelProperty(value = "发邮件给管理人(用户id,用逗号隔开)")
    @Column(name = "manager")
    private String manager;
}
