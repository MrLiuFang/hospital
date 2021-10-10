package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.WardRoom;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/6 上午10:39
 */
@Data
@Schema
public class ListWardRoomVo extends WardRoom {

    @Schema(description = "总床位数量")
    private int total;

    @Schema(description = "已使用床位数量")
    private int useTotal;
}
