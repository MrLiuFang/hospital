package com.lion.manage.entity.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.enums.ExposeObject;
import com.lion.manage.entity.enums.RegionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午9:35
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_region_expose_object")
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "区域公开对象")
public class RegionExposeObject extends BaseEntity {

    @ApiModelProperty(value = "区域id")
    @Column(name = "region_id",nullable = false)
    @NotNull(message = "区域不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long regionId;

    @ApiModelProperty(value = "公开对象")
    @Column(name = "expose_object",nullable = false)
    @Convert(converter = ExposeObject.ExposeObjectConverter.class)
    @NotNull(message = "公开对象不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private ExposeObject exposeObject;
}
