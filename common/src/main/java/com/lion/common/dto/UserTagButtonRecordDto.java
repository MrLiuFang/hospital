package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:09
 */
@Data
public class UserTagButtonRecordDto implements Serializable {

    private static final long serialVersionUID = 320853505628827188L;
    @ApiModelProperty(value = "员工id")
    private Long pi;

    @ApiModelProperty(value = "tagid")
    private Long ti;

    @ApiModelProperty(value = "按钮编号(1,2,3,4)")
    private Integer bi;

    @ApiModelProperty(value = "按钮事件名称")
    private String bn;

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
