package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AlarmClassify;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 上午9:33
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm_way",indexes = {@Index(columnList = "alarm_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "警报方式")
public class AlarmWay extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7542149250412572073L;
    @Schema(description = "警报id")
    @Column(name = "alarm_id")
    @NotNull(message = "{2000044}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long alarmId;

    @Schema(description = "警报方式")
    @Column(name = "way")
    @Convert(converter = com.lion.manage.entity.enums.AlarmWay.AlarmWayConverter.class)
    @NotNull(message = "{2000045}", groups = {Validator.Insert.class, Validator.Update.class})
    private com.lion.manage.entity.enums.AlarmWay way;
}
