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
 * @Date 2021/5/18 下午3:16
 **/
@Data
@Schema
public class WashRecordDto implements Serializable {

    private static final long serialVersionUID = -7868264693079166765L;
    @Id
    private String _id;

    @Schema(description = "用于与洗手事件唯一关联标识(区域洗手),定时洗手没有此关联")
    private String ui;

    @Schema(description = "洗手规则Id")
    private Long wi;

    @Schema(description = "员工id/患者id/流动人员id")
    private Long pi;

    @Schema(description = "员工类型 UserType")
    private Long py;

    @Schema(description = "员工所在的科室id")
    private Long pdi;

    @Schema(description = "员工所在的科室名称")
    private String pdn;

    @Schema(description = "洗手的设备id")
    private Long dvi;

    @Schema(description = "洗手的设备名称")
    private String dvn;

    @Schema(description = "洗手的设备编码")
    private String dvc;

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

    @Schema(description = "洗手时长")
    private Integer t = 0;

    @Schema(description = "设备产生的洗手時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @Schema(description = "系统接收到的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;

    @Schema(description = "cctvUrl-多个用逗号隔开")
    private String cctvUrl;
}
