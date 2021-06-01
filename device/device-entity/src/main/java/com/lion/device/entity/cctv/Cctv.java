package com.lion.device.entity.cctv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.State;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:12
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_cctv",indexes = {@Index(columnList = "name"),@Index(columnList = "model")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "cctv")
public class Cctv extends BaseEntity {

    @ApiModelProperty(value = "cctv名称")
    @Column(name = "name")
    @NotBlank(message = "cctv名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "cctv型号")
    @Column(name = "model")
    private String model;

    @ApiModelProperty(value = "cctv编号")
    @Column(name = "code")
    @NotNull(message = "cctv编号不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @ApiModelProperty(value = "图片")
    @Column(name = "img")
    private Long img;

    @ApiModelProperty(value = "ip地址")
    @Column(name = "ip")
    @NotBlank(message = "ip地址不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String ip;

    @ApiModelProperty(value = "端口")
    @Column(name = "port")
//    @NotNull(message = "端口不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer port;

    @ApiModelProperty(value = "建筑id（关联区域自动更新该值）")
    @Column(name = "build_id")
    private Long buildId;

    @ApiModelProperty(value = "楼层id（关联区域自动更新该值）")
    @Column(name = "build_floor_id")
    private Long buildFloorId;

    @ApiModelProperty(value = "区域Id（关联区域自动更新该值）")
    @Column(name = "region_id")
    private Long regionId;

    @ApiModelProperty(value = "科室Id（关联区域自动更新该值）")
    @Column(name = "department_id")
    private Long departmentId;

    @ApiModelProperty(value = "设备状态")
    @Column(name = "device_sate")
    @Convert(converter = State.StateConverter.class)
    private State deviceSate = State.NORMAL;

    @ApiModelProperty(value = "最后的设备数据时间")
    @Column(name = "last_data_time")
    private LocalDateTime lastDataTime;
}
