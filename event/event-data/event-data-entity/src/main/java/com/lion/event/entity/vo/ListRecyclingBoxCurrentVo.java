package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 20:15
 **/
@Data
@Schema
public class ListRecyclingBoxCurrentVo {

    @Schema(description = "回收箱id")
    private Long recyclingBoxId;

    @Schema(description = "回收箱名称")
    private String name;

    @Schema(description = "回收箱编号")
    private String code;

    @Schema(description = "标签数量")
    private Integer count;

    @Schema(description = "上次消毒时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate previousDisinfectDate;


}
