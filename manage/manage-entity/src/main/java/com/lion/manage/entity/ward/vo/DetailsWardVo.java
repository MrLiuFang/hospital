package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:15
 */
@Data
@Schema
public class DetailsWardVo extends Ward {

    @Schema(description = "病房房间")
    private List<DetailsWardRoomVo> wardRoom;

}
