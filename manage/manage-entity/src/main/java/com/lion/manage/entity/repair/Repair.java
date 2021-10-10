package com.lion.manage.entity.repair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "维修")
public class Repair extends BaseEntity {

    @Schema(description = "科室ID")
    private Long departmentId;

    @Schema(description = "区域ID")
    private Long regionId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "html")
    @Column(name = "html",length = 2000)
    private String html;


    @Schema(description = "设备ID用逗号隔开")
    private String deviceId;
}
