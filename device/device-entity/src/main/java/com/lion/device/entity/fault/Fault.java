package com.lion.device.entity.fault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.FaultType;
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
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:18
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_fault" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "故障表")
public class Fault extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8725223620163785910L;
    @ApiModelProperty(value = "关联ID（设备ID/cctvId/……）")
    @Column(name = "relation_id")
    private Long relationId;

    @ApiModelProperty(value = "故障类型")
    @Column(name = "type")
    @NotNull(message = "故障类型不能为空",groups = {Validator.Update.class,Validator.Insert.class})
    private FaultType type;

    @ApiModelProperty(value = "资产编码/其它编码")
    @NotBlank(message = "编码不能为空",groups = {Validator.Update.class,Validator.Insert.class})
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "所属区域")
    @Column(name = "region_id")
    private Long regionId;

    @ApiModelProperty(value = "所属建筑")
    @Column(name = "build_id")
    private Long buildId;

    @ApiModelProperty(value = "所属建筑楼层")
    @Column(name = "build_floor_id")
    private Long buildFloorId;

    @ApiModelProperty(value = "所属科室")
    @Column(name = "department_id")
    private Long departmentId;

    @ApiModelProperty(value = "申报人编号")
    @Column(name = "user_number")
    @NotNull(message = "申报人编号不能为空",groups = {Validator.Update.class,Validator.Insert.class})
    private Integer userNumber;

    @ApiModelProperty(value = "故障描述")
    @Column(name = "content",length = 2000)
    @NotBlank(message = "故障描述不能为空",groups = {Validator.Update.class,Validator.Insert.class})
    private String content;

    @ApiModelProperty(value = "是否已处理")
    @Column(name = "is_solve")
    private Boolean isSolve = false;
}
