package com.lion.device.entity.fault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.FaultType;
import io.swagger.v3.oas.annotations.media.Schema;

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

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "故障表")
public class Fault extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8725223620163785910L;
    @Schema(description = "关联ID（设备ID/cctvId/……）")
    @Column(name = "relation_id")
    private Long relationId;

    @Schema(description = "故障类型")
    @Column(name = "type")
    @NotNull(message = "{4000012}",groups = {Validator.Update.class,Validator.Insert.class})
    private FaultType type;

    @Schema(description = "资产编码/其它编码")
    @NotBlank(message = "{4000013}",groups = {Validator.Update.class,Validator.Insert.class})
    @Column(name = "code")
    private String code;

    @Schema(description = "所属区域")
    @Column(name = "region_id")
    private Long regionId;

    @Schema(description = "所属建筑")
    @Column(name = "build_id")
    private Long buildId;

    @Schema(description = "所属建筑楼层")
    @Column(name = "build_floor_id")
    private Long buildFloorId;

    @Schema(description = "所属科室")
    @Column(name = "department_id")
    private Long departmentId;

    @Schema(description = "申报人编号")
    @Column(name = "user_number")
    @NotNull(message = "{4000014}",groups = {Validator.Update.class,Validator.Insert.class})
    private Integer userNumber;

    @Schema(description = "故障描述")
    @Column(name = "content",length = 2000)
    @NotBlank(message = "{4000015}",groups = {Validator.Update.class,Validator.Insert.class})
    private String content;

    @Schema(description = "是否已处理")
    @Column(name = "is_solve")
    private Boolean isSolve = false;
}
