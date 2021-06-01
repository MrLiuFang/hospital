package com.lion.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 上午10:46
 */
@Data
@ApiModel
public class UnalarmDto {

    @ApiModelProperty(value = "_id")
    private String id;

    @ApiModelProperty(value = "uuid")
    private String uuid;
}
