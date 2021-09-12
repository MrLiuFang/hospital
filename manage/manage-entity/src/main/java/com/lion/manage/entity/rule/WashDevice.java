package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:41
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_wash_device",indexes = {@Index(columnList ="wash_id" )})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "洗手规则设备")
public class WashDevice extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4703857505736813459L;
    @ApiModelProperty(value = "洗手规则id")
    @Column(name = "wash_id")
    @NotNull(message = "{2000049}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long washId;

    @ApiModelProperty(value = "洗手设备id")
    @Column(name = "device_id")
    @NotNull(message = "{2000050}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long deviceId;
}
