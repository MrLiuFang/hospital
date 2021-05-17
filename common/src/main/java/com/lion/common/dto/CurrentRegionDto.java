package com.lion.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午4:00
 **/
@Data
public class CurrentRegionDto  {

    @ApiModelProperty(value = "当前所在的区域Id")
    private Long regionId;

    @ApiModelProperty(value = "第一次进入时间")
    private LocalDateTime firstEntryTime;
}
