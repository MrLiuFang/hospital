package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.enums.TagUseState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7上午9:04
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "标签")
public class Tag extends BaseEntity {

    @ApiModelProperty(value = "标签分类")
    @Convert(converter = TagType.TagTypeConverter.class)
    @NotNull(message = "标签分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "type",nullable = false)
    private TagType type;

    @ApiModelProperty(value = "标签分类")
    @Convert(converter = TagPurpose.TagPurposeConverter.class)
    @NotNull(message = "标签分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "purpose",nullable = false)
    private TagPurpose purpose;

    @ApiModelProperty(value = "标签编码")
    @NotBlank(message = "标签编码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_code",unique = true)
    private String tagCode;

    @ApiModelProperty(value = "所属科室")
    @Column(name = "department_id",nullable = false)
    @NotNull(message = "所属科室不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @ApiModelProperty(value = "设备名称")
    @NotBlank(message = "设备名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "device_name",unique = true)
    private String deviceName;

    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "设备编码不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "device_code",unique = true)
    private String deviceCode;

    @ApiModelProperty(value = "最高温度")
    @Column(name = "max_temperature")
    private BigDecimal maxTemperature;

    @ApiModelProperty(value = "最低温度")
    @Column(name = "min_temperature")
    private BigDecimal minTemperature;

    @ApiModelProperty(value = "最高湿度")
    @Column(name = "max_humidity")
    private BigDecimal maxHumidity;

    @ApiModelProperty(value = "最低湿度")
    @Column(name = "min_humidity")
    private BigDecimal minHumidity;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    @Column(name = "battery")
    private Integer battery;

    @ApiModelProperty(value = "使用状态")
    @Column(name = "use_state")
    private TagUseState useState;

}
