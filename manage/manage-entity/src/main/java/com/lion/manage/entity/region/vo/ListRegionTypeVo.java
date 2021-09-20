package com.lion.manage.entity.region.vo;

import com.lion.manage.entity.region.RegionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午8:53
 */
@Data
@ApiModel
public class ListRegionTypeVo extends RegionType {

    @ApiModelProperty(value = "关联数量")
    private Integer count;
}
