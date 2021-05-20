package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午8:47
 */
@Data
@ApiModel(value = "区域统计")
public class RegionStatisticsDetails {

    @ApiModelProperty(value = "员工数量")
    private Integer staffCount;

    @ApiModelProperty(value = "患者数量")
    private Integer patientCount;

    @ApiModelProperty(value = "资产数量")
    private Integer assetsCount;

    @ApiModelProperty(value = "温湿标签数量")
    private Integer tagCount;

    @ApiModelProperty(value = "流动人员数量")
    private Integer migrantCount;

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "区域坐标组")
    private String coordinates;

    @ApiModelProperty(value = "是否发生警告")
    private Boolean isAlarm;
}
