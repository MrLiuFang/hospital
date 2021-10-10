package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.AlarmDuration;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13上午10:33
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm",indexes = {@Index(columnList = "content"),@Index(columnList = "classify"),@Index(columnList = "level")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "警报规则")
public class Alarm extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4020836313788285118L;
    @Schema(description = "警报分类")
    @Column(name = "classify")
    @Convert(converter = AlarmClassify.AlarmClassifyConverter.class)
    @NotNull(message = "{2000037}", groups = {Validator.Insert.class, Validator.Update.class})
    private AlarmClassify classify;

    @Schema(description = "级别(仅限患者分类下的(1,2,3级))")
    @Column(name = "level")
    private Integer level;

    @Schema(description = "排序")
    @Column(name = "sort")
    @NotNull(message = "{2000038}", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer sort;

    @Schema(description = "警报编码")
    @Column(name = "code",updatable = false)
    @Convert(converter = SystemAlarmType.SystemAlarmTypeConverter.class)
    @NotNull(message = "{2000039}", groups = {Validator.Insert.class,Validator.Update.class})
    private SystemAlarmType code;

    @Schema(description = "警报内容")
    @Column(name = "content")
    @NotBlank(message = "{2000040}", groups = {Validator.Insert.class, Validator.Update.class})
    private String content;

    @Schema(description = "警报声持续时间")
    @Column(name = "duration")
    @Convert(converter = AlarmDuration.AlarmDurationConverter.class)
    @NotNull(message = "{2000041}", groups = {Validator.Insert.class, Validator.Update.class})
    private AlarmDuration duration;

    @Schema(description = "blueCode")
    @Column(name = "blue_code")
    private String blueCode;

    @Schema(description = "再次提醒未处理警报事件")
    @Column(name = "again")
    @NotNull(message = "{2000042}", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean again = false;

    @Schema(description = "提醒间隔时间")
    @Column(name = "Interval")
    private Integer Interval;

    @Schema(description = "是否发邮件给科室负责人")
    @Column(name = "is_send_mail_to_department_manager")
    @NotNull(message = "{2000043}", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean isSendMailToDepartmentManager = false;

    @Schema(description = "发邮件给管理人(用户id,用逗号隔开)")
    @Column(name = "manager")
    private String manager;
}
