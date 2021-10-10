package com.lion.event.entity.vo;

import com.lion.event.entity.WashEvent;
import com.lion.manage.entity.enums.SystemAlarmType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema
public class ListViolationWashEventVo extends WashEvent {

    @Schema(description = "不合规原因")
    private SystemAlarmType alarmType;

    @Schema(description = "不合规原因说明")
    private String alarmTypeStr;

    @Schema(description = "姓名")
    private String name;
}

