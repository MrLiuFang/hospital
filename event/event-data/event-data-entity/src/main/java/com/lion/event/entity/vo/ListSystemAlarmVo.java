package com.lion.event.entity.vo;

import com.lion.event.entity.SystemAlarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午9:09
 */
@Data
@ApiModel
public class ListSystemAlarmVo extends SystemAlarm {

    @ApiModelProperty(value = "警告内容")
    private String alarmContent;

    @ApiModelProperty(value = "警告内容编码(com.lion.manage.entity.enums.SystemAlarmType 获取该字典)")
    private String alarmCode;
}
