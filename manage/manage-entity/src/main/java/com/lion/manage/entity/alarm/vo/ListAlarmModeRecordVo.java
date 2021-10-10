package com.lion.manage.entity.alarm.vo;

import com.lion.manage.entity.alarm.AlarmModeRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:14
 */
@Data
@Schema
public class ListAlarmModeRecordVo extends AlarmModeRecord {

    @Schema(description = "切换人姓名")
    private String name;

    @Schema(description = "切换人头像id")
    private Long headPortrait;

    @Schema(description = "切换人头像url")
    private String headPortraitUrl;
}
