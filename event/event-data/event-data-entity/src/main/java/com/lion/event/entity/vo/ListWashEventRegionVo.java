package com.lion.event.entity.vo;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/7 下午2:23
 */
@Data
@Schema
public class ListWashEventRegionVo {

    @Schema(description = "区域id")
    private Long regionId;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "设备数量")
    private Integer deviceCount = 0;

    @Schema(description = "合规率")
    private BigDecimal ratio;
}
