package com.lion.device.entity.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:56
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_device" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "设备")
public class Device extends BaseEntity {

    @ApiModelProperty(value = "设备名称")
    @Column(name = "name",unique = true)
    @NotBlank(message = "设备名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "code",unique = true)
    @NotNull(message = "设备编号不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @ApiModelProperty(value = "设备大类")
    @Convert(converter = DeviceClassify.DeviceClassifyConverter.class)
    @NotNull(message = "设备大类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "device_classify")
    private DeviceClassify deviceClassify;

    @ApiModelProperty(value = "设备分类")
    @Convert(converter = DeviceType.DeviceTypeConverter.class)
    @NotNull(message = "设备分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "device_type")
    private DeviceType deviceType;

    @ApiModelProperty(value = "保修期(月)")
    @Column(name = "warranty_period",nullable = false)
    @NotNull(message = "保修期不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer warrantyPeriod;

    @ApiModelProperty(value = "购买日期(yyyy-MM-dd)")
    @Column(name = "purchase_pate",nullable = true)
    @NotNull(message = "购买日期不能为空", groups = {Validator.Insert.class, Validator.Update.class})

    @Past(message = "购买日期不能大于/等于当前日期", groups = {Validator.Insert.class, Validator.Update.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @ApiModelProperty(value = "保修期截止日(根据购买日期+保修期(月)推算)")
    @Column(name = "warranty_period_date",nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyPeriodDate;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    @Column(name = "battery")
    private Integer battery;

    @ApiModelProperty(value = "图片id")
    @Column(name = "img")
    private Long img;

    @ApiModelProperty(value = "建筑id(安装位置)")
    @Column(name = "build_id")
    private Long buildId;

    @ApiModelProperty(value = "楼层id(安装位置)")
    @Column(name = "build_floor_id")
    private Long buildFloorId;

    @ApiModelProperty(value = "地图X坐标(安装位置)")
    @Column(name = "x")
    private String x;

    @ApiModelProperty(value = "地图Y坐标(安装位置)")
    @Column(name = "y")
    private String y;
}
