package com.lion.manage.entity.department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/27 下午3:46
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_department_alarm")
@DynamicInsert

@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"departmentId","createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "科室警告设置")
public class DepartmentAlarm extends BaseEntity {

    @ApiModelProperty(value = "科室id")
    private Long departmentId;

    @ApiModelProperty(value = "患者离开科室")
    private Boolean leaveDepartment = true;

    @ApiModelProperty(value = "患者标签电池电量不足")
    private Boolean patientBattery = true;

    @ApiModelProperty(value = "员工标签电池电量不足")
    private Boolean staffBattery = true;

    @ApiModelProperty(value = "员工未遵循洗手规则")
    private Boolean staffWash = true;

    @ApiModelProperty(value = "资产标签电池电量不足")
    private Boolean assetsBattery = true;

    @ApiModelProperty(value = "温湿标签电池电量不足")
    private Boolean humidBattery = true;

    @ApiModelProperty(value = "定位设备电池电量不足")
    private Boolean monitorBattery = true;

    @ApiModelProperty(value = "资产未登记被移出存放区域")
    private Boolean assetsLeave = true;
}
