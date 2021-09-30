package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 上午10:20
 */
@Data
@ApiModel
public class RegionStatisticsDetailsVo {

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

    @ApiModelProperty(value = "组网设备数量")
    private Integer cctvCount;

    @ApiModelProperty(value = "今天员工访问数量")
    private Integer todayStaffCount;

    @ApiModelProperty(value = "今天患者访问数量")
    private Integer todayPatientCount;

    @ApiModelProperty(value = "今天资产访问数量")
    private Integer todayAssetsCount;

    @ApiModelProperty(value = "今天流动人员访问数量")
    private Integer todayMigrantCount;
}
