package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午10:27
 **/
@Data
@ApiModel
public class UserCurrentRegionVo {

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "建筑id")
    private Long buildId;

    @ApiModelProperty(value = "建筑名称")
    private String buildName;

    @ApiModelProperty(value = "楼层id")
    private Long buildFloorId;

    @ApiModelProperty(value = "楼层名称")
    private String buildFloorName;

    @ApiModelProperty(value = "科室id")
    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "进入时间")
    private LocalDateTime firstEntryTime;

}
