package com.lion.manage.entity.build;

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
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:02
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_build_floor" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "楼层")
public class BuildFloor extends BaseEntity {

    @ApiModelProperty(value = "建筑id")
    @NotBlank(message = "建筑名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "build_id",nullable = false)
    private Long buildId;

    @ApiModelProperty(value = "地图url")
    @NotBlank(message = "地图url名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "map_url",nullable = false)
    private String mapUrl;

    @ApiModelProperty(value = "楼层名称")
    @Column(name = "name",nullable = false)
    @NotBlank(message = "楼层名称不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @ApiModelProperty(value = "备注")
    @Column(name = "remarks")
    private String remarks;
}
