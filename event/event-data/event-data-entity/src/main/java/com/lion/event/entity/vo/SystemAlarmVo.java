package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Type;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.SystemAlarm;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.annotations.Tag;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午7:40
 */
@Data
@Schema
public class SystemAlarmVo extends SystemAlarm {

    @Schema(description = "警告来源(com.lion.common.enums.Type 获取该字典)")
    private Type type;

    @Schema(description = "标签码")
    private String tagCode;

    @Schema(description = "标签属性")
    private TagType tagType;

    @Schema(description = "警告内容")
    private String alarmContent;

    @Schema(description = "警告内容编码(com.lion.manage.entity.enums.SystemAlarmType 获取该字典)")
    private String alarmCode;

    @Schema(description = "警告人姓名/资产名称/tag名称.......")
    private String title;

    @Schema(description = "图片ID")
    private Long imgId;

    @Schema(description = "图片url")
    private String imgUrl;

    @Schema(description = "警告发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deviceDateTime;

    @Schema(description = "排序时间（循环警报提醒时置顶显示）前端可判断该时间是否发生变化来进行声音/闪耀提醒")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sortDateTime;
}
