package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //警告事件
 * @Date 2021/5/1 上午11:24
 **/
@Data
@Document(value = "system_alarm")
@ApiModel
public class SystemAlarm implements Serializable {

    private static final long serialVersionUID = 8357470689294387808L;
    @Id
    private String _id;

    /**
     * 唯一追踪标识
     */
    @ApiModelProperty(value = "唯一追踪标识")
    private String ui;

    @ApiModelProperty(value = "警告类型(com.lion.common.enums.Type)")
    private Integer ty;

    @ApiModelProperty(value = "员工/患者/流动人员id")
    private Long pi;

    @ApiModelProperty(value = "资产id")
    private Long ai;

    @ApiModelProperty(value = "设备id")
    private Long dvi;

    @ApiModelProperty(value = "标签id")
    private Long ti;

    @ApiModelProperty(value = "警告id")
    private Long ali;

    @ApiModelProperty(value = "温湿标签警告湿度")
    private BigDecimal h;

    @ApiModelProperty(value = "温湿标签标签警告温度")
    private BigDecimal t;

    @ApiModelProperty(value = "警告类型(com.lion.manage.entity.enums.SystemAlarmType)")
    private Integer sat;

    @ApiModelProperty(value = "是否已知熟（处理）(1=已处理,0=未处理)")
    private Integer ua = false ? 1 : 0;

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

    @ApiModelProperty(value = "警告发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dt;

    @ApiModelProperty(value = "排序时间（用户多次警报时置顶显示）前端可判断该时间是否发生变化来进行声音/闪耀提醒")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;

    @ApiModelProperty(value = "处理人id")
    private Long uui;

    @ApiModelProperty(value = "处理人姓名")
    private String uun;

    @ApiModelProperty(value = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime udt;


    @ApiModelProperty(value = "汇报人id")
    private Long rui;

    @ApiModelProperty(value = "汇报人姓名")
    private String run;

    @ApiModelProperty(value = "汇报员工编号")
    private Integer rnu;

    @ApiModelProperty(value = "员工汇报内容")
    private String re="";

    @ApiModelProperty(value = "汇报时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rdt;

    public void setUa(Boolean ua) {
        this.ua = ua ? 1 : 0;
    }

}
