package com.lion.event.entity.dto;

import com.lion.upms.entity.enums.AlarmMode;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:58
 */
@Data
@Schema
public class SetAlarmModeDto {

    @Schema(description = "模式")
    private AlarmMode alarmMode;

    @Schema(description = "密码-MD5")
    private String password;
}
