package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-18 10:27
 **/
@Data
@Schema
@Builder
public class SevenDaysStatisticsVo {

    @Schema(description = "日期")
    @JsonFormat(pattern = "MM-dd")
    private LocalDate date;

    @Schema(description = "数量")
    private int count;
}
