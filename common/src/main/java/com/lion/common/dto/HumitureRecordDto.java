package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午8:29
 **/
@Data
public class HumitureRecordDto implements Serializable {

    private static final long serialVersionUID = -4561410568391847197L;
    @ApiModelProperty(value = "类型 (com.lion.common.enums.Type)")
    private Integer typ;

    @ApiModelProperty(value = "tagid")
    private Long ti;

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

    @ApiModelProperty(value = "温度")
    private BigDecimal t;

    @ApiModelProperty(value = "湿度")
    private BigDecimal h;

    @ApiModelProperty(value = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}

