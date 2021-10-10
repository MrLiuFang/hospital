package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午8:29
 **/
@Data
@Schema
public class HumitureRecordDto implements Serializable {

    private static final long serialVersionUID = -4561410568391847197L;
    @Schema(description = "类型 (com.lion.common.enums.Type)")
    private Integer typ;

    @Schema(description = "tagid")
    private Long ti;

    @Schema(description = "建筑id")
    private Long bui;

    @Schema(description = "建筑名称")
    private String bun;

    @Schema(description = "楼层id")
    private Long bfi;

    @Schema(description = "楼层名称")
    private String bfn;

    @Schema(description = "所属科室id")
    private Long sdi;

    @Schema(description = "科室id")
    private Long di;

    @Schema(description = "科室名称")
    private String dn;

    @Schema(description = "区域id")
    private Long ri;

    @Schema(description = "区域名称")
    private String rn;

    @Schema(description = "温度")
    private BigDecimal t;

    @Schema(description = "湿度")
    private BigDecimal h;

    @Schema(description = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @Schema(description = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}

