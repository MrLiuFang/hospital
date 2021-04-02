package com.lion.manage.entity.ward.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.ward.Ward;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午10:08
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel
public class AddWardDto extends Ward {

    @ApiModelProperty(value = "病房房间")
    private List<AddWardRoomDto> wardRoom;
}
