package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:15
 */
@Data
@ApiModel
public class DetailsWardVo extends Ward {

    @ApiModelProperty(value = "病房房间")
    private List<DetailsWardRoomVo> wardRoom;

}
