package com.lion.device.entity.cctv;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:12
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_cctv",indexes = {@Index(columnList = "name"),@Index(columnList = "model")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime"})
@Schema(description = "cctv")
public class Cctv extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 9015253379019012364L;
    @Schema(description = "cctv名称")
    @Column(name = "name")
    @NotBlank(message = "{4000000}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "cctv型号")
    @Column(name = "model")
    private String model;

    @Schema(description = "cctv编号")
    @Column(name = "code")
    @NotNull(message = "{4000001}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @Schema(description = "图片")
    @Column(name = "img")
    private Long img;

    @Schema(description = "ip地址")
    @Column(name = "ip")
    @NotBlank(message = "{4000002}", groups = {Validator.Insert.class, Validator.Update.class})
    private String ip;

    @Schema(description = "端口")
    @Column(name = "port")
//    @NotNull(message = "端口不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer port;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "建筑id（关联区域自动更新该值）")
    @Column(name = "build_id")
    private Long buildId;

    @Schema(description = "楼层id（关联区域自动更新该值）")
    @Column(name = "build_floor_id")
    private Long buildFloorId;

    @Schema(description = "区域Id（关联区域自动更新该值）")
    @Column(name = "region_id")
    private Long regionId;

    @Schema(description = "科室Id（关联区域自动更新该值）")
    @Column(name = "department_id")
    private Long departmentId;

    @Schema(description = "是否在线")
    private Boolean isOnline = false;

    @Schema(description = "是否启用")
    private Boolean isEnable =false;

//    @Schema(description = "设备状态")
//    @Column(name = "device_state")
//    @Convert(converter = State.StateConverter.class)
//    private State deviceState = State.NOT_USED;

//    @Schema(description = "最后的设备数据时间")
//    @Column(name = "last_data_time")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime lastDataTime;
}
