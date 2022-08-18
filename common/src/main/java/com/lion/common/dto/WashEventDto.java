package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 下午3:51
 **/
@Data
@Schema
public class WashEventDto extends WashRecordDto implements Serializable {

    private static final long serialVersionUID = 7308806002975275332L;
    @Schema(description = "用于统计所有员工(仅限sql语法统计,无实际含义)")
    private Integer a = 0;

    @Schema(description = "洗手事件类型 (com.lion.common.enums.WashEventType)")
    private Integer wet;

    @Schema(description = "是否触发警告(合规/不合规)")
    private Boolean ia = false;

    @Schema(description = "触发警告原因(com.lion.manage.entity.enums.SystemAlarmType)")
    private Integer at;

//    @Schema(description = "洗手时间(针对区域洗手规则-有记录为违规洗手,没有记录为错过洗手)定时洗手规则一律为错过洗手(9999-01-01 00:00:00 为没有记录 小于该时间为有时间记录)")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime wt = LocalDateTime.parse("9999-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Schema(description = "触发警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime adt = LocalDateTime.now();
}
