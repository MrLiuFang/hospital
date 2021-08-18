package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-18 14:12
 **/
@Data
@Builder
@ApiModel
public class TodayDaysStatisticsVo {

    @ApiModelProperty(value = "警报总数")
    private int total;

    @ApiModelProperty(value = "患者数量")
    private int patientCount;

    @ApiModelProperty(value = "职员数量")
    private int staffCount;

    @ApiModelProperty(value = "资产数量")
    private int assetsCount;

    @ApiModelProperty(value = "温湿数量")
    private int humidCount;
}
