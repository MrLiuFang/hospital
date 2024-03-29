package com.lion.event.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //手卫生监控
 * @Date 2021/5/11 下午4:43
 **/
@Data
@Schema
public class ListWashMonitorVo {

    @Schema(description = "科室合规率")
    private List<Ratio> department;

    @Schema(description = "全院合规率")
    private Ratio hospital;

    @Data
    @Schema
    public static class Ratio {

        @Schema(description = "全院/科室名称")
        private String name;

        @Schema(description = "合规")
        private Integer conformance;

        @Schema(description = "所有事件(合规+不合规)")
        private Integer allCount;

        @Schema(description = "违规")
        private Integer violation;

        @Schema(description = "错过洗手")
        private Integer noWash;

        public Integer getAllCount() {
            return violation+noWash+conformance;
        }
    }
}
