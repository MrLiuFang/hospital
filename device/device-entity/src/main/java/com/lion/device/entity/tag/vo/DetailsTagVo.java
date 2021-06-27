package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午3:15
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"lastDataTime","deviceState","createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsTagVo extends Tag {

    @ApiModelProperty(value = "绑定对象")
    private String bindingName;

    @ApiModelProperty(value = "绑定对象图片")
    private Long img;

    @ApiModelProperty(value = "绑定对象图片Url")
    private String imgUrl;
}
