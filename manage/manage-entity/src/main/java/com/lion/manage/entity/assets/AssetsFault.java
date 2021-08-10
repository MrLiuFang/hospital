package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.enums.AssetsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午2:35
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets_fault",indexes = {@Index(columnList = "assets_id")}  )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "资产故障")
public class AssetsFault extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4868910888962338123L;
    @ApiModelProperty(value = "资产Id")
    @Column(name = "assets_id")
    @NotNull(message = "资产id不能为空", groups = {Validator.Insert.class})
    private Long assetsId;

    @ApiModelProperty(value = "故障编码")
    @Column(name = "code",updatable = false)
    @NotBlank(message = "故障编码不能为空", groups = {Validator.Insert.class})
    private String code;

    @ApiModelProperty(value = "故障描述")
    @Column(name = "describe" ,updatable = false)
    @NotBlank(message = "故障描述不能为空", groups = {Validator.Insert.class})
    private String describe;

    @ApiModelProperty(value = "申报人")
    @Column(name = "declarant_user_id",updatable = false)
    @NotNull(message = "申报人不能为空", groups = {Validator.Insert.class})
    private Long declarantUserId;

    @ApiModelProperty(value = "状态")
    @Column(name = "state")
    @Convert(converter = AssetsFaultState.AssetsFaultStateConverter.class)
    @NotNull(message = "状态不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private AssetsFaultState state = AssetsFaultState.NOT_FINISHED;

    @ApiModelProperty(value = "申报时间(yyyy-MM-dd HH:mm:ss)")
    @Column(name = "declarant_time",updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime declarantTime;

    @ApiModelProperty(value = "完成时间(yyyy-MM-dd HH:mm:ss)")
    @Column(name = "finish_time",insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

//    @ApiModelProperty(value = "完成人")
//    @Column(name = "finish_user_id",insertable = false)
//    @NotNull(message = "完成人不能为空", groups = {Validator.Update.class})
//    private Long finishUserId;
}
