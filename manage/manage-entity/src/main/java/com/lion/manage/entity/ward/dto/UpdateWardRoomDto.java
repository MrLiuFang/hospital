package com.lion.manage.entity.ward.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.ward.WardRoom;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午10:11
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"wardId","id","createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema
public class UpdateWardRoomDto extends WardRoom {

    @Schema(description = "床位(先删后增-全量传给后台)")
    private List<UpdateWardRoomSickbedDto> wardRoomSickbed;
}
