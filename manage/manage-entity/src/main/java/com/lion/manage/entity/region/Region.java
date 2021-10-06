package com.lion.manage.entity.region;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:11
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_region" )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "区域")
public class Region extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -5542281222157892159L;
    @ApiModelProperty(value = "区域名称")
    @Column(name = "name")
    @NotBlank(message = "{2000026}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "区域编号")
    @Column(name = "code")
    @NotBlank(message = "{2000109}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @ApiModelProperty(value = "位置编号")
    @Column(name = "position_code")
    private String positionCode;

    @ApiModelProperty(value = "通行级别")
    @Column(name = "traffic_level")
    @NotNull(message = "{2000110}", groups = {Validator.Insert.class, Validator.Update.class})
    private Integer trafficLevel;

    @ApiModelProperty(value = "是否公开")
    @Column(name = "is_public")
    @NotNull(message = "{2000027}", groups = {Validator.Insert.class, Validator.Update.class})
    public Boolean isPublic = false;

//    @ApiModelProperty(value = "分类")
//    @Column(name = "type")
//    @Convert(converter = RegionType.RegionTypeConverter.class)
//    @NotNull(message = "{2000028}", groups = {Validator.Insert.class, Validator.Update.class})
//    public RegionType type;

    @ApiModelProperty(value = "科室")
    @Column(name = "department_id")
    @NotNull(message = "{2000029}", groups = {Validator.Insert.class, Validator.Update.class})
    public Long departmentId;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty(value = "区域坐标组")
    @Column(name = "coordinates",length = 5000)
    @NotBlank(message = "{2000030}", groups = {Validator.Insert.class,Validator.OtherOne.class})
    private String coordinates;

//    @ApiModelProperty(value = "设备组id")
//    @Column(name = "device_group_id")
//    @NotNull(message = "{2000031}", groups = {Validator.Insert.class})
//    private Long deviceGroupId;

    @ApiModelProperty(value = "建筑id")
    @Column(name = "build_id")
    @NotNull(message = "{2000032}", groups = {Validator.Insert.class})
    private Long buildId;

    @ApiModelProperty(value = "建筑楼层id")
    @Column(name = "build_floor_id")
    @NotNull(message = "{2000033}", groups = {Validator.Insert.class})
    private Long buildFloorId;

    @ApiModelProperty("洗手规则模板id")
    private Long washTemplateId;

    @ApiModelProperty("区域类型id")
    @NotNull(message = "{2000108}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long regionTypeId;

    @ApiModelProperty("设备数量定义")
    private String deviceQuantityDefinition;

//    [{"code":"STAR_AP"},{"code":"MONITOR"},{"code":"VIRTUAL_WALL","count":"2"},{"code":"LF_EXCITER"},{"code":"HAND_WASHING"},{"code":"RECYCLING_BOX"}]
}
