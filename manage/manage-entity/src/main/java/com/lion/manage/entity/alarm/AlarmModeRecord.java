package com.lion.manage.entity.alarm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.upms.entity.enums.AlarmMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午7:52
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm_mode_record")

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "警告模式切换记录")
public class AlarmModeRecord extends BaseEntity {

    @ApiModelProperty(name = "切换用户id")
    private Long userId;

    @ApiModelProperty(name = "警告模式")
    @Convert(converter = AlarmMode.AlarmModeConverter.class)
    private AlarmMode alarmMode;
}
