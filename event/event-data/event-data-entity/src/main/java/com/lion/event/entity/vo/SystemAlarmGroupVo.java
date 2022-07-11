package com.lion.event.entity.vo;

import com.lion.device.entity.enums.TagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @classname SystemAlarmGroupVo
 * @description
 * @date 2022/04/13 下午5:15
 */
@Data
public class SystemAlarmGroupVo {

    @Schema(description = "标签Id")
    private Long tagId;

    @Schema(description = "标签码")
    private String tagCode;

    @Schema(description = "标签属性")
    private TagType tagType;

    @Schema(description = "警告数量")
    private Integer count;

    @Schema(description = "警告人姓名/资产名称/tag名称.......")
    private String title;

    @Schema(description = "图片ID")
    private Long imgId;

    @Schema(description = "图片url")
    private String imgUrl;

    @Schema(description = "告警内容")
    private SystemAlarmVo systemAlarm;

    @Schema(description = "告警时间")
    private LocalDateTime dateTime;

}
