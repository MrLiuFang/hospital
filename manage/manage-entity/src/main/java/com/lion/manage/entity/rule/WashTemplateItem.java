package com.lion.manage.entity.rule;

import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

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
@ApiModel(description = "洗手规则模板项")
public class WashTemplateItem extends BaseEntity {

    private static final long serialVersionUID = -2130429637033735604L;
    @ApiModelProperty("洗手规则模板id")
    private Long washTemplateId;

    @ApiModelProperty("false=普通模式，true=传染/紧急模式")
    private Boolean isUrgent;

    @ApiModelProperty("免洗手间隔时长")
    private Integer noCheckTime;

    @ApiModelProperty("检测洗手时间-进入区域前")
    private Integer beforeTime;

    @ApiModelProperty("检测洗手时间-进入区域后")
    private Integer afterTime;

    @ApiModelProperty("检测洗手时间-离开区域后")
    private Integer leaveTime;

    @ApiModelProperty("发起提醒")
    private Boolean isRemind;

    @ApiModelProperty("发起告警")
    private Boolean isAlarm;

    @ApiModelProperty("触发警示铃")
    private Boolean isBell;

}
