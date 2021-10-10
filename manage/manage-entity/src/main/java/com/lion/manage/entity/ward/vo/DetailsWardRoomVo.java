package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:16
 */
@Data
@Schema
public class DetailsWardRoomVo extends WardRoom {

    @Schema(description = "病床")
    private List<WardRoomSickbed> wardRoomSickbed;
}
