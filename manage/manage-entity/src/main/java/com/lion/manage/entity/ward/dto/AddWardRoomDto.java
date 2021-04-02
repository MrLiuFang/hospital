package com.lion.manage.entity.ward.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午10:11
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"wardId","id","createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel
public class AddWardRoomDto extends WardRoom {

    @ApiModelProperty(value = "床位")
    private List<AddWardRoomSickbedDto> wardRoomSickbed;
}
