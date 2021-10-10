package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:17
 */
@Data
@Schema
public class RecyclingBoxRecordDto implements Serializable {

    private static final long serialVersionUID = 5169920111697099159L;
    @Schema(description = "回收箱id")
    private Long rbi;

    @Schema(description = "回收箱名称")
    private String rbn;

    @Schema(description = "回收箱编码")
    private String rbc;

    @Schema(description = "tagid")
    private Long ti;

    @Schema(description = "tag用途")
    private Integer tp;

    @Schema(description = "tag类型")
    private Integer tt;

    @Schema(description = "tag编码")
    private String tc;

    @Schema(description = "建筑id")
    private Long bui;

    @Schema(description = "建筑名称")
    private String bun;

    @Schema(description = "楼层id")
    private Long bfi;

    @Schema(description = "楼层名称")
    private String bfn;

    @Schema(description = "科室id")
    private Long di;

    @Schema(description = "科室名称")
    private String dn;

    @Schema(description = "区域id")
    private Long ri;

    @Schema(description = "区域名称")
    private String rn;

    @Schema(description = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @Schema(description = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
