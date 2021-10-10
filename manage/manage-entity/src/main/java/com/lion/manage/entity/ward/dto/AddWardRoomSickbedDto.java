package com.lion.manage.entity.ward.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午10:12
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"wardRoomId","id","createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema
public class AddWardRoomSickbedDto extends WardRoomSickbed {
}
