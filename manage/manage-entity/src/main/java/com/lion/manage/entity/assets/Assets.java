package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.enums.State;
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
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午2:34
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets")

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "资产")
public class Assets extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2129036984572348881L;
    @Schema(description = "资产名称")
    @Column(name = "name")
    @NotBlank(message = "{2000000}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "资产编号")
    @Column(name = "code")
    @NotBlank(message = "{2000001}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @Schema(description = "资产分类")
    @Column(name = "assets_type_id")
//    @Convert(converter = AssetsType.AssetsTypeConverter.class)
//    @NotNull(message = "{2000002}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long assetsTypeId;

    @Schema(description = "所属区域")
    @Column(name = "region_id")
    @NotNull(message = "{2000003}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long regionId;

    @Schema(description = "所属建筑")
    @Column(name = "build_id")
//    @NotNull(message = "所属建筑不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long buildId;

    @Schema(description = "所属建筑楼层")
    @Column(name = "build_floor_id")
//    @NotNull(message = "所属建筑楼层不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long buildFloorId;

    @Schema(description = "所属科室")
    @Column(name = "department_id")
//    @NotNull(message = "所属科室不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @Schema(description = "使用状态")
    @Column(name = "use_state")
    @Convert(converter = AssetsUseState.AssetsUseStateConverter.class)
//    @NotNull(message = "使用状态不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AssetsUseState useState = AssetsUseState.NOT_USED;

    @Schema(description = "是否需要使用登记")
    @Column(name = "use_registration")
    @NotNull(message = "{2000004}", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean useRegistration;

    @Schema(description = "图片")
    @Column(name = "img")
    private Long img;

    @Schema(description = "备注")
    @Column(name = "remarks")
    private String remarks;

    @Schema(description = "设备状态")
    @Column(name = "device_state")
    @Convert(converter = com.lion.manage.entity.enums.State.StateConverter.class)
    private com.lion.manage.entity.enums.State deviceState = State.NORMAL;

    @Schema(description = "最后的设备数据时间")
    @Column(name = "last_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastDataTime;
}
