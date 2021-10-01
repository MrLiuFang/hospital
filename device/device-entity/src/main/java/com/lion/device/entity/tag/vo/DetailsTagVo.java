package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午3:15
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"lastDataTime","deviceState","createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsTagVo extends Tag {

    @ApiModelProperty(value = "绑定Id")
    private Long bindingId;

    @ApiModelProperty(value = "绑定对象")
    private String bindingName;

    @ApiModelProperty(value = "绑定对象图片")
    private Long img;

    @ApiModelProperty(value = "绑定对象图片Url")
    private String imgUrl;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "当前温度")
    private BigDecimal temperature;

    @ApiModelProperty(value = "当前湿度")
    private BigDecimal humidity;

    @ApiModelProperty(value = "温湿度获取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private String alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警报ID")
    private String alarmId;

    @ApiModelProperty(value = "24小时温度")
    private List<Temperature24hour> temperature24hour;

    @ApiModelProperty(value = "24小时湿度")
    private List<Humidity24hour> humidity24hour;

    @Data
    @ApiModel
    @Builder
    public static class Temperature24hour {

        @ApiModelProperty(value = "时间")
        private LocalDateTime dateTime;

        @ApiModelProperty(value = "温度")
        private BigDecimal temperature;
    }

    @Data
    @ApiModel
    @Builder
    public static class Humidity24hour {

        @ApiModelProperty(value = "时间")
        private LocalDateTime dateTime;

        @ApiModelProperty(value = "湿度")
        private BigDecimal humidity;

    }

}
