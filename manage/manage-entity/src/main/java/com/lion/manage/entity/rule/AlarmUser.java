package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @classname AlarmUser
 * @description
 * @date 2022/04/10 上午10:30
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_alarm_user")
@DynamicInsert
@Data
public class AlarmUser extends BaseEntity {

    private static final long serialVersionUID = 7542149250412572073L;

    @Schema(description = "警报id")
    private Long alarmId;

    @Schema(description = "用户id")
    private Long userId;

}
