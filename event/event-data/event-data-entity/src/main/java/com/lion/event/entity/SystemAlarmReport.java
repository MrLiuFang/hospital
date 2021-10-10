package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午8:11
 */
@Data
@Document(value = "system_alarm_report")
@Schema
public class SystemAlarmReport implements Serializable {
    private static final long serialVersionUID = -5913271797850294815L;

    @Id
    private String _id;

    private String sli;

    @Schema(description = "汇报人id")
    private Long rui;

    @Schema(description = "汇报人姓名")
    private String run;

    @Schema(description = "员工汇报内容")
    private String re;

    @Schema(description = "汇报时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rdt;
}
