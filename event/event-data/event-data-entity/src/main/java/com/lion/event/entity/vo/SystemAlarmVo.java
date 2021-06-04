package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Type;
import com.lion.event.entity.SystemAlarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午7:40
 */
@Data
@ApiModel
public class SystemAlarmVo extends SystemAlarm {

    @ApiModelProperty(value = "警告来源(com.lion.common.enums.Type 获取该字典)")
    private Type type;

    @ApiModelProperty(value = "警告内容")
    private String alarmContent;

    @ApiModelProperty(value = "警告内容编码(com.lion.manage.entity.enums.SystemAlarmType 获取该字典)")
    private String alarmCode;

    @ApiModelProperty(value = "警告人姓名/资产名称/tag名成.......")
    private String title;

    @ApiModelProperty(value = "图片ID")
    private Long imgId;

    @ApiModelProperty(value = "图片url")
    private String imgUrl;

    @ApiModelProperty(value = "警告发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deviceDateTime;

    @ApiModelProperty(value = "排序时间（循环警报提醒时置顶显示）前端可判断该时间是否发生变化来进行声音/闪耀提醒")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sortDateTime;
}
