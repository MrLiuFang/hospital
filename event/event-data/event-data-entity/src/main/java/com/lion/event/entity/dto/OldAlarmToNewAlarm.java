package com.lion.event.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 上午10:48
 */
@Data
@Schema
public class OldAlarmToNewAlarm {

    @Schema(description = "_id")
    private String id;
}
