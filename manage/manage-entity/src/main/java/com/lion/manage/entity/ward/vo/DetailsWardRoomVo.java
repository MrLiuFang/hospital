package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:16
 */
@Data
@ApiModel
public class DetailsWardRoomVo extends WardRoom {

    @ApiModelProperty(value = "病床")
    private List<WardRoomSickbed> wardRoomSickbed;
}
