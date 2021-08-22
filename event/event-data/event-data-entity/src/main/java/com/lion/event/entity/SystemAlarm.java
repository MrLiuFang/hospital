package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.SystemAlarmState;
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

    @ApiModelProperty(value = "警告规则id")
    private Long ali;

    @ApiModelProperty(value = "温湿标签警告湿度")
    private BigDecimal h;

    @ApiModelProperty(value = "温湿标签标签警告温度")
    private BigDecimal t;

    @ApiModelProperty(value = "警告类型(com.lion.manage.entity.enums.SystemAlarmType)")
    private Integer sat;

    @ApiModelProperty(value = "警告状态（com.lion.manage.entity.enums.SystemAlarmState)（0, 未处理),(1, 已处理(熟知)操作员处理),(2, 主动呼叫),(3, 取消呼叫),(4, 警告熟知(员工通过按钮熟知)")
    private Integer ua;

    @ApiModelProperty(value = "建筑id")
    private Long bui;

    @ApiModelProperty(value = "建筑名称")
    private String bun;

    @ApiModelProperty(value = "楼层id")
    private Long bfi;

    @ApiModelProperty(value = "楼层名称")
    private String bfn;

    @ApiModelProperty(value = "警告所属科室id")
    private Long sdi;

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

    public void setUa(SystemAlarmState state) {
        this.ua = state.getKey();
    }

}
