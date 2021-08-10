package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 20:15
 **/
@Data
@ApiModel
public class ListRecyclingBoxCurrentVo {

    @ApiModelProperty(value = "回收箱id")
    private Long recyclingBoxId;

    @ApiModelProperty(value = "回收箱名称")
    private String name;

    @ApiModelProperty(value = "回收箱编号")
    private String code;

    @ApiModelProperty(value = "标签数量")
    private Integer count;

    @ApiModelProperty(value = "上次消毒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate previousDisinfectDate;


}
