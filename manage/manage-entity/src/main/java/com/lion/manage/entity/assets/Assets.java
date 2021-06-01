package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AssetsType;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.enums.State;
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
 * @date 2021/4/6下午2:34
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets")
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "资产")
public class Assets extends BaseEntity {

    @ApiModelProperty(value = "资产名称")
    @Column(name = "name")
    @NotBlank(message = "资产名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "资产编号")
    @Column(name = "code")
    @NotBlank(message = "资产编号不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @ApiModelProperty(value = "资产分类")
    @Column(name = "type")
    @Convert(converter = AssetsType.AssetsTypeConverter.class)
    @NotNull(message = "资产分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AssetsType type;

    @ApiModelProperty(value = "所属区域")
    @Column(name = "region_id")
    @NotNull(message = "所属区域不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long regionId;

    @ApiModelProperty(value = "所属建筑")
    @Column(name = "build_id")
//    @NotNull(message = "所属建筑不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long buildId;

    @ApiModelProperty(value = "所属建筑楼层")
    @Column(name = "build_floor_id")
//    @NotNull(message = "所属建筑楼层不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long buildFloorId;

    @ApiModelProperty(value = "所属科室")
    @Column(name = "department_id")
//    @NotNull(message = "所属科室不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @ApiModelProperty(value = "使用状态")
    @Column(name = "use_state")
    @Convert(converter = AssetsUseState.AssetsUseStateConverter.class)
//    @NotNull(message = "使用状态不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AssetsUseState useState = AssetsUseState.NOT_USED;

    @ApiModelProperty(value = "是否需要使用登记")
    @Column(name = "use_registration")
    @NotNull(message = "是否需要使用登记不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Boolean useRegistration;

    @ApiModelProperty(value = "图片")
    @Column(name = "img")
    private Long img;

    @ApiModelProperty(value = "设备状态")
    @Column(name = "device_sate")
    @Convert(converter = com.lion.manage.entity.enums.State.StateConverter.class)
    private com.lion.manage.entity.enums.State deviceSate = State.NORMAL;

    @ApiModelProperty(value = "最后的设备数据时间")
    @Column(name = "last_data_time")
    private LocalDateTime lastDataTime;
}
