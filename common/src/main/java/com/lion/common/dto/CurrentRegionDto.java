package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Hygiene;
import com.lion.common.enums.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午4:00
 **/
@Data
public class CurrentRegionDto implements Serializable {

    private static final long serialVersionUID = -5394114715986856960L;

    @ApiModelProperty(value = "当前所在的区域Id")
    private Long regionId;

    @ApiModelProperty(value = "第一次进入时间")
    private LocalDateTime firstEntryTime;

    @ApiModelProperty(value = "设备产生的时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "系统接收到的时间")
    private LocalDateTime systemDateTime;
}
