package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/6 上午11:15
 */
@Data
@ApiModel
public class ListWardRoomSickbedVo extends WardRoomSickbed {

    @ApiModelProperty(value = "是否已使用")
    private Boolean isUse;
}
