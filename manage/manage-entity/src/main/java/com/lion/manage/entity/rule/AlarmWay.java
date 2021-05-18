package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AlarmClassify;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/18 上午9:33
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm_way",indexes = {@Index(columnList = "alarm_id")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "警报方式")
public class AlarmWay extends BaseEntity {

    @ApiModelProperty(value = "警报id")
    @Column(name = "alarm_id")
    @NotNull(message = "警报id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long alarmId;

    @ApiModelProperty(value = "警报方式")
    @Column(name = "way")
    @Convert(converter = com.lion.manage.entity.enums.AlarmWay.AlarmWayConverter.class)
    @NotNull(message = "警报方式不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private com.lion.manage.entity.enums.AlarmWay way;
}
