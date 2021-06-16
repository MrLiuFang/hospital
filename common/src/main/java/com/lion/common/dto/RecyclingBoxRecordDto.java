package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:17
 */
@Data
@ApiModel
public class RecyclingBoxRecordDto {

    @ApiModelProperty(value = "回收箱id")
    private Long rbi;

    @ApiModelProperty(value = "回收箱名称")
    private String rbn;

    @ApiModelProperty(value = "回收箱编码")
    private String rbc;

    @ApiModelProperty(value = "tagid")
    private Long ti;

    @ApiModelProperty(value = "tag用途")
    private Integer tp;

    @ApiModelProperty(value = "tag类型")
    private Integer tt;

    @ApiModelProperty(value = "tag编码")
    private String tc;

    @ApiModelProperty(value = "建筑id")
    private Long bui;

    @ApiModelProperty(value = "建筑名称")
    private String bun;

    @ApiModelProperty(value = "楼层id")
    private Long bfi;

    @ApiModelProperty(value = "楼层名称")
    private String bfn;

    @ApiModelProperty(value = "科室id")
    private Long di;

    @ApiModelProperty(value = "科室名称")
    private String dn;

    @ApiModelProperty(value = "区域id")
    private Long ri;

    @ApiModelProperty(value = "区域名称")
    private String rn;

    @ApiModelProperty(value = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
