package com.lion.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 上午10:48
 */
@Data
@ApiModel
public class OldAlarmToNewAlarm {

    @ApiModelProperty(value = "_id")
    private String id;
}
