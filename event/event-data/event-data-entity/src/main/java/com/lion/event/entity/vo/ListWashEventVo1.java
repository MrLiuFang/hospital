package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.WashState;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.event.entity.WashEvent;
import com.lion.upms.entity.enums.Gender;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/6 上午10:03
 */
@Data
@Schema
public class ListWashEventVo1 extends WashEvent {

    @Schema(description = "状态")
    private WashState state;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "设备类型")
    private DeviceType dvt;

}
