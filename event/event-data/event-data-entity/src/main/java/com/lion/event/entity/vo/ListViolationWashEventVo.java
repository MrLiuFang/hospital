package com.lion.event.entity.vo;

import com.lion.event.entity.WashEvent;
import com.lion.manage.entity.enums.SystemAlarmType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ListViolationWashEventVo extends WashEvent {

    @ApiModelProperty(value = "不合规原因")
    private SystemAlarmType alarmType;

    @ApiModelProperty(value = "不合规原因说明")
    private String alarmTypeStr;

    @ApiModelProperty(value = "姓名")
    private String name;
}

