package com.lion.device.entity.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.swing.*;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午9:07
 **/
@Data
@ApiModel
public class DeviceStatisticsVo {

    @ApiModelProperty(value = "统计")
    private List<DeviceStatisticsData> list;

    @Data
    @ApiModel
    public static class DeviceStatisticsData{
        @ApiModelProperty(value = "统计名称")
        private String name;

        @ApiModelProperty(value = "编码")
        private String code;

        @ApiModelProperty(value = "统计数量")
        private long count;
    }
}
