package com.lion.event.entity.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/7 下午2:23
 */
@Data
@ApiModel
public class ListWashEventRegionVo {

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "设备数量")
    private Integer deviceCount = 0;

    @ApiModelProperty(value = "合规率")
    private BigDecimal ratio;
}
