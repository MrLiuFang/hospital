package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午3:15
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"lastDataTime","deviceState","createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsTagVo extends Tag {

    @Schema(description = "绑定Id")
    private Long bindingId;

    @Schema(description = "绑定对象")
    private String bindingName;

    @Schema(description = "绑定对象图片")
    private Long img;

    @Schema(description = "绑定对象图片Url")
    private String imgUrl;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "当前温度")
    private BigDecimal temperature;

    @Schema(description = "当前湿度")
    private BigDecimal humidity;

    @Schema(description = "温湿度获取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private String alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警报ID")
    private String alarmId;

    @Schema(description = "24小时温度")
    private List<Temperature24hour> temperature24hour;

    @Schema(description = "24小时湿度")
    private List<Humidity24hour> humidity24hour;

    @Data
    @Schema
    @Builder
    public static class Temperature24hour {

        @Schema(description = "时间")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime time;

        @Schema(description = "温度")
        private BigDecimal temperature;
    }

    @Data
    @Schema
    @Builder
    public static class Humidity24hour {

        @Schema(description = "时间")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime time;

        @Schema(description = "湿度")
        private BigDecimal humidity;

    }
}
