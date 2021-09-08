package com.lion.device.entity.device;

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
 * @date 2021/3/31上午11:19
 */
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name = "t_device_group_device")
//@DynamicUpdate
//@DynamicInsert
//@Data
//@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
//@ApiModel(description = "设备组关联的设备")
//public class DeviceGroupDevice extends BaseEntity implements Serializable {
//
//    private static final long serialVersionUID = -5786186112857864557L;
//    @ApiModelProperty(value = "设备组id")
//    @Column(name = "device_group_id")
//    @NotNull(message = "{4000010}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Long deviceGroupId;
//
//    @ApiModelProperty(value = "设备id")
//    @Column(name = "device_id")
//    @NotNull(message = "{4000011}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Long deviceId;
//}
