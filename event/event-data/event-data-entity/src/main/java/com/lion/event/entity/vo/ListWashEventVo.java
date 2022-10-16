package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.enums.Gender;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/6 上午10:03
 */
@Data
@Schema
public class ListWashEventVo {


    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "科室")
    private String departmentName;

    @Schema(description = "工号")
    private Integer number;

    @Schema(description = "类型")
    private UserType userType;

    @Schema(description = "性别")
    private Gender gender;

    @Schema(description = "使用设备")
    private String deviceName;

    @Schema(description = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useDateTime;

    @Schema(description = "洗手事件时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Schema(description = "是否合规")
    private Boolean ia;

    @Schema(description = "洗手时长")
    private Integer time;

    @Schema(description = "cctv-可能多个逗号隔开")
    private String cctvUrl;
}
