package com.lion.manage.entity.region.vo;

import com.lion.manage.entity.region.RegionType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午8:53
 */
@Data
@Schema
public class ListRegionTypeVo extends RegionType {

    @Schema(description = "关联数量")
    private Integer count;
}
