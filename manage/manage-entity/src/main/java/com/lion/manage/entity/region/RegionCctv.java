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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:01
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_region_cctv" )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "区域关联的cctv")
public class RegionCctv extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1422275392378141382L;
    @ApiModelProperty(value = "区域id")
    @Column(name = "region_id")
    @NotNull(message = "{2000034}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long regionId;

    @ApiModelProperty(value = "cctvid")
    @Column(name = "cctv_id")
    @NotNull(message = "{2000035}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long cctvId;
}
