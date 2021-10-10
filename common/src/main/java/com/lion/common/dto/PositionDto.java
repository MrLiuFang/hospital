package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午4:20
 **/
@Data
@Schema
public class PositionDto implements Serializable {

    private static final long serialVersionUID = 6996838306863461533L;
    @Id
    private String _id;

    @Schema(description = "类型 (com.lion.common.enums.Type)")
    private Integer typ;

    @Schema(description = "员工/患者/流动人员id")
    private Long pi;

    @Schema(description = "设备/资产id")
    private Long adi;

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

    @Schema(description = "科室id")
    private Long di;

    @Schema(description = "科室名称")
    private String dn;

    @Schema(description = "区域id")
    private Long ri;

    @Schema(description = "区域名称")
    private String rn;

    @Schema(description = "x坐标")
    private String x;


    @Schema(description = "y坐标")
    private String y;

    @Schema(description = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @Schema(description = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;

    @Schema(description = "离开时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ldt;

    @Schema(description = "时长(分钟)")
    private Integer t;
}
