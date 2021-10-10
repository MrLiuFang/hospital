package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午10:44
 */
@Data
@Schema
public class DepartmentTagStatisticsDetailsVo {

    @Schema(description = "标签总数")
    private Integer tagCount = 0;

    @Schema(description = "正常标签总数")
    private Integer normalTagCount= 0;

    @Schema(description = "异常标签总数")
    private Integer abnormalTagCount= 0;

    @Schema(description = "部门信息")
    private List<TagDepartmentVo> tagDepartmentVos;

    @Schema
    @Data
    public static class TagDepartmentVo {

        @Schema(description = "科室id")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;

        @Schema(description = "标签信息")
        private List<DepartmentTagStatisticsDetailsVo.TagVo> tagVos;
    }

    @Schema
    @Data
    public static class TagVo extends Tag {

        @Schema(description = "上次温度")
        private BigDecimal previousTemperature;

        @Schema(description = "上次湿度")
        private BigDecimal previousHumidity;

        @Schema(description = "当前温度")
        private BigDecimal temperature;

        @Schema(description = "当前湿度")
        private BigDecimal humidity;

        @Schema(description = "数据记录时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dataDateTime;

        @Schema(description = "上次数据记录时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime previousDataDateTime;
    }

}
