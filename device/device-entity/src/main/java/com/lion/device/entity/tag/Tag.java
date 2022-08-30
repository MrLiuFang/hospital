package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;

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
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7上午9:04
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag" )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"lastDataTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "标签")
public class Tag extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 610297207210388788L;
    @Schema(description = "标签分类")
    @Convert(converter = TagType.TagTypeConverter.class)
    @NotNull(message = "{4000016}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "type")
    private TagType type;

    @Schema(description = "标签用途")
    @Convert(converter = TagPurpose.TagPurposeConverter.class)
    @NotNull(message = "{4000017}", groups = {Validator.Insert.class})
    @Column(name = "purpose")
    private TagPurpose purpose;

    @Schema(description = "标签编码")
    @NotBlank(message = "{4000018}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_code")
    private String tagCode;

    @Schema(description = "所属科室")
    @Column(name = "department_id")
    @NotNull(message = "{0000009}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @Schema(description = "设备名称")
    @Column(name = "device_name")
    private String deviceName;

    @Schema(description = "设备编码")
    @Column(name = "device_code")
    private String deviceCode;

    @Schema(description = "最高温度")
    @Column(name = "max_temperature")
    private BigDecimal maxTemperature;

    @Schema(description = "最低温度")
    @Column(name = "min_temperature")
    private BigDecimal minTemperature;

    @Schema(description = "最高湿度")
    @Column(name = "max_humidity")
    private BigDecimal maxHumidity;

    @Schema(description = "最低湿度")
    @Column(name = "min_humidity")
    private BigDecimal minHumidity;

    @Schema(description = "电量(0=正常,1=少於90 天,2=少於30天)")
    @Column(name = "battery")
    private Integer battery;

//    @Schema(description = "使用使用状态")
//    @Column(name = "use_state")
//    private TagUseState useState = TagUseState.NOT_USED;

//    @Schema(description = "状态")
//    @Column(name = "state")
//    @Convert(converter = TagState.TagStateConverter.class)
//    private TagState state = TagState.NORMAL;

    @Schema(description = "设备状态")
    @Column(name = "device_state")
    @Convert(converter = com.lion.device.entity.enums.State.StateConverter.class)
    private com.lion.device.entity.enums.State deviceState = State.NOT_USED;

    @Schema(description = "是否告警")
    private Boolean isAlarm;

    @Schema(description = "是否故障")
    private Boolean isFault;

    @Schema(description = "是否在线")
    private Boolean iSOnline;

    @Schema(description = "最后的设备数据时间")
    @Column(name = "last_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastDataTime;

    @Schema(description = "tagRssi")
    private String tagRssi;

}
