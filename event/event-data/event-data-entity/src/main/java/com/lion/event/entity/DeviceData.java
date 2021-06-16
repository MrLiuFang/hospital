package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //事件原始数据
 * @Date 2021/4/23 上午9:45
 **/
@Data
@Document(value = "device_data")
@ApiModel
public class DeviceData implements Serializable {

    private static final long serialVersionUID = -2540094505107531820L;
    @Id
    private String _id;

    @ApiModelProperty(value = "员工/患者/流动人员id")
    private Long pi;

    //类型 (com.lion.common.enums.Type)
    @ApiModelProperty(value = "数据类型")
    private Integer typ;

    @ApiModelProperty(value = "Star code")
    private String sc;

    @ApiModelProperty(value = "Star名称")
    private String sn;

    //Star大类(com.lion.device.entity.enums.DeviceClassify)
    @ApiModelProperty(value = "Star大类")
    private Integer scl;

    //star类型(com.lion.device.entity.enums.DeviceType)
    @ApiModelProperty(value = "star类型")
    private Integer st;

    @ApiModelProperty(value = "Monitor code")
    private String mc;

    @ApiModelProperty(value = "Monitor名称")
    private String mn;

    //Monitor大类(com.lion.device.entity.enums.DeviceClassify)
    @ApiModelProperty(value = "Monitor大类")
    private Integer mcl;

    //Monitor类型(com.lion.device.entity.enums.DeviceType)
    @ApiModelProperty(value = "Monitor类型")
    private Integer mt;

    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    @ApiModelProperty(value = "Monitor 的電量")
    private Integer mb;

    @ApiModelProperty(value = "Tag code")
    private String tc;

    //Tag名称
    @ApiModelProperty(value = "Tag名称")
    private String tn;

    //tag类型(com.lion.device.entity.enums.TagType)
    @ApiModelProperty(value = "tag类型")
    private Integer tt;

    //tag用途(com.lion.device.entity.enums.TagPurpose)
    @ApiModelProperty(value = "tag用途")
    private Integer tp;

    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    @ApiModelProperty(value = "Tag 的電量")
    private Integer tb;

    @ApiModelProperty(value = "Tag 按下按鈕 1-4")
    private Integer bi;

    @ApiModelProperty(value = "溫度")
    private BigDecimal t;

    @ApiModelProperty(value = "湿度")
    private BigDecimal h;

    @ApiModelProperty(value = "警告")
    private String w;

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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "设备产生時間")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接受到时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
