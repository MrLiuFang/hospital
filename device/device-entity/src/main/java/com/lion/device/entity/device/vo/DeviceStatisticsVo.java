package com.lion.device.entity.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.swing.*;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午9:07
 **/
@Data
@Schema
public class DeviceStatisticsVo {

    @Schema(description = "统计")
    private List<DeviceStatisticsData> list;

    @Data
    @Schema
    public static class DeviceStatisticsData{
        @Schema(description = "统计名称")
        private String name;

        @Schema(description = "编码")
        private String code;

        @Schema(description = "统计数量")
        private long count;
    }
}
