package com.lion.manage.entity.region;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:08
 */

import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_region_warning_bell" )
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "区域警示铃")
public class RegionWarningBell extends BaseEntity {

    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "warning_bell_id")
    private Long warningBellId;
}
