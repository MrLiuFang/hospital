package com.lion.manage.entity.rule;

import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:28
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_wash_template_item")

@DynamicInsert
@Data
@Schema(description = "洗手规则模板项")
public class WashTemplateItem extends BaseEntity {

    private static final long serialVersionUID = -2130429637033735604L;
    @Schema(description = "洗手规则模板id")
    private Long washTemplateId;

    @Schema(description = "false=普通模式，true=传染/紧急模式")
    @NotNull(groups = {Validator.Update.class,Validator.Insert.class})
    private Boolean isUrgent=false;

    @Schema(description = "免洗手间隔时长")
    private Integer noCheckTime;

    @Schema(description = "检测洗手时间-进入区域前")
    private Integer beforeTime;

    @Schema(description = "检测洗手时间-进入区域后")
    private Integer afterTime;

    @Schema(description = "检测洗手时间-离开区域后")
    private Integer leaveTime;

    @Schema(description = "发起提醒")
    private Boolean isRemind;

    @Schema(description = "发起告警")
    private Boolean isAlarm;

    @Schema(description = "触发警示铃")
    private Boolean isBell;

}
