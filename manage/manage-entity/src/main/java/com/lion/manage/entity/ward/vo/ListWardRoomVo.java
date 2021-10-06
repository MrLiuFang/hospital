package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.WardRoom;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/6 上午10:39
 */
@Data
@ApiModel
public class ListWardRoomVo extends WardRoom {

    @ApiModelProperty(value = "总床位数量")
    private int total;

    @ApiModelProperty(value = "已使用床位数量")
    private int useTotal;
}
