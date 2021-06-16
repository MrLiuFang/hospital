package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午4:20
 **/
@Data
@ApiModel
public class PositionDto implements Serializable {

    private static final long serialVersionUID = 6996838306863461533L;
    @Id
    private String _id;

    @ApiModelProperty(value = "类型 (com.lion.common.enums.Type)")
    private Integer typ;

    @ApiModelProperty(value = "员工/患者/流动人员id")
    private Long pi;

    @ApiModelProperty(value = "设备/资产id")
    private Long adi;

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

    @ApiModelProperty(value = "设备产生時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接受到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
