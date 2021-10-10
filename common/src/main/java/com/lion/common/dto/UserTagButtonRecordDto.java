package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:09
 */
@Data
@Schema
public class UserTagButtonRecordDto implements Serializable {

    private static final long serialVersionUID = 320853505628827188L;
    @Schema(description = "员工id")
    private Long pi;

    @Schema(description = "tagid")
    private Long ti;

    @Schema(description = "按钮编号(1,2,3,4)")
    private Integer bi;

    @Schema(description = "按钮事件名称")
    private String bn;

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
