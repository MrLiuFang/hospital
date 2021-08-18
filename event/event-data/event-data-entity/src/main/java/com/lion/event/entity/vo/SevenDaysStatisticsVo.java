package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
@Builder
public class SevenDaysStatisticsVo {

    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "MM-dd")
    private LocalDate date;

    @ApiModelProperty(value = "数量")
    private int count;
}
