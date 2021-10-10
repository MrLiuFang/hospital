package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/6 上午11:15
 */
@Data
@Schema
public class ListWardRoomSickbedVo extends WardRoomSickbed {

    @Schema(description = "是否已使用")
    private Boolean isUse;
}
