package com.lion.manage.entity.alarm.vo;

import com.lion.manage.entity.alarm.AlarmModeRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:14
 */
@Data
@ApiModel
public class ListAlarmModeRecordVo extends AlarmModeRecord {

    @ApiModelProperty(name = "切换人姓名")
    private String name;

    @ApiModelProperty(name = "切换人头像id")
    private Long headPortrait;

    @ApiModelProperty(name = "切换人头像url")
    private String headPortraitUrl;
}
