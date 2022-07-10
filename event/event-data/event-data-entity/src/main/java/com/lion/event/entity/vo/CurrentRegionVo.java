package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午10:27
 **/
@Data
@Schema
public class CurrentRegionVo {

    @Schema(description = "区域id")
    private Long regionId;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "建筑id")
    private Long buildId;

    @Schema(description = "建筑名称")
    private String buildName;

    @Schema(description = "楼层id")
    private Long buildFloorId;

    @Schema(description = "楼层名称")
    private String buildFloorName;

    @Schema(description = "科室id")
    private Long departmentId;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "")
    private String x;

    @Schema(description = "")
    private String y;

    @Schema(description = "进入时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime firstEntryTime;

}
