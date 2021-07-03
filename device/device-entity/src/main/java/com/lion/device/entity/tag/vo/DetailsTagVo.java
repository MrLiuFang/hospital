package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime dateTime;


}
