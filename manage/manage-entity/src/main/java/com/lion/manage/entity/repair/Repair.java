package com.lion.manage.entity.repair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 下午2:53
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_repair" )
@DynamicInsert
@Data
@ApiModel(description = "维修")
public class Repair extends BaseEntity {

    @ApiModelProperty(value = "科室ID")
    private Long departmentId;

    @ApiModelProperty(value = "区域ID")
    private Long regionId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "html")
    @Column(name = "html",length = 2000)
    private String html;


    @ApiModelProperty(value = "设备ID用逗号隔开")
    private String deviceId;
}
