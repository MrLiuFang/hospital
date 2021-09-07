package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.WashRuleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/14 上午9:59
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_wash_device_type",indexes = {@Index(columnList ="wash_id" )})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "洗手规则设备类型")
public class WashDeviceType extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6794032758736876013L;
    @ApiModelProperty(value = "洗手规则id/洗手规则模板项Id")
    @Column(name = "wash_id")
    @NotNull(message = "{2000049}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long washId;

    @ApiModelProperty(value = "洗手设备类型")
    @Column(name = "type")
    @Convert(converter = com.lion.manage.entity.enums.WashDeviceType.WashDeviceTypeConverter.class)
    @NotNull(message = "{2000051}", groups = {Validator.Insert.class, Validator.Update.class})
    private com.lion.manage.entity.enums.WashDeviceType type;
}
