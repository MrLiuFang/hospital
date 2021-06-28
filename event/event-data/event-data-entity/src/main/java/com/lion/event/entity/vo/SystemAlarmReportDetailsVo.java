package com.lion.event.entity.vo;

import com.lion.event.entity.SystemAlarmReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午2:11
 */
@Data
@ApiModel
public class SystemAlarmReportDetailsVo extends SystemAlarmReport {

    @ApiModelProperty(value = "汇报人头像")
    private Long headPortrait;

    @ApiModelProperty(value = "汇报人头像Url")
    private String headPortraitUrl;
}
