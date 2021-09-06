package com.lion.event.entity.dto;

import com.lion.upms.entity.enums.AlarmMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:58
 */
@Data
@ApiModel
public class SetAlarmModeDto {

    @ApiModelProperty(value = "模式")
    private AlarmMode alarmMode;

    @ApiModelProperty(value = "密码-MD5")
    private String password;
}
