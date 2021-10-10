package com.lion.event.entity.vo;

import com.lion.event.entity.SystemAlarmReport;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午2:11
 */
@Data
@Schema
public class SystemAlarmReportDetailsVo extends SystemAlarmReport {

    @Schema(description = "汇报人头像")
    private Long headPortrait;

    @Schema(description = "汇报人头像Url")
    private String headPortraitUrl;
}
