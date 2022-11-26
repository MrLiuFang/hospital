package com.lion.device.entity.tag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午9:07
 **/
@Data
@Schema
public class TagStatisticsVo {

    @Schema(description = "统计")
    private List<TagStatisticsData> list;

    @Data
    @Schema
    public static class TagStatisticsData{
        @Schema(description = "统计名称")
        private String name;

        @Schema(description = "编码")
        private String code;

        @Schema(description = "统计数量")
        private long count;
    }
}
