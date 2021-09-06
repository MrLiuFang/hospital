package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/18 下午3:16
 **/
@Data
public class WashRecordDto implements Serializable {

    private static final long serialVersionUID = -7868264693079166765L;
    @Id
    private String _id;

    @ApiModelProperty(value = "用于与洗手事件唯一关联标识(区域洗手),定时洗手没有此关联")
    private String ui;

    @ApiModelProperty(value = "洗手规则Id")
    private Long wi;

    @ApiModelProperty(value = "员工id/患者id/流动人员id")
    private Long pi;

    @ApiModelProperty(value = "员工类型 UserType")
    private Long py;

    @ApiModelProperty(value = "员工所在的科室id")
    private Long pdi;

    @ApiModelProperty(value = "员工所在的科室名称")
    private String pdn;

    @ApiModelProperty(value = "洗手的设备id")
    private Long dvi;

    @ApiModelProperty(value = "洗手的设备名称")
    private String dvn;

    @ApiModelProperty(value = "洗手的设备编码")
    private String dvc;

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

    @ApiModelProperty(value = "洗手时长")
    private Integer t;

    @ApiModelProperty(value = "设备产生的洗手時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接收到的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
