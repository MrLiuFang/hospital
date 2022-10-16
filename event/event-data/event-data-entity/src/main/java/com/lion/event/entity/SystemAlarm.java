package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.SystemAlarmState;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class SystemAlarm implements Serializable {

    private static final long serialVersionUID = 8357470689294387808L;
    @Id
    private String _id;

    @Schema(description = "警告类型(com.lion.common.enums.Type)")
    private Integer ty;

    @Schema(description = "员工/患者/流动人员id")
    private Long pi;

    @Schema(description = "资产id")
    private Long ai;

    @Schema(description = "设备id")
    private Long dvi;

    @Schema(description = "标签id")
    private Long ti;

    @Schema(description = "警告规则id")
    private Long ali;

    @Schema(description = "温湿标签警告湿度")
    private BigDecimal h;

    @Schema(description = "温湿标签标签警告温度")
    private BigDecimal t;

    @Schema(description = "警告类型(com.lion.manage.entity.enums.SystemAlarmType)")
    private Integer sat;

    @Schema(description = "警告状态（com.lion.manage.entity.enums.SystemAlarmState)（0, 未处理),(1, 已处理(熟知)操作员处理),(2, 主动呼叫),(3, 取消呼叫),(4, 警告熟知(员工通过按钮熟知)")
    private Integer ua;

    @Schema(description = "建筑id")
    private Long bui;

    @Schema(description = "建筑名称")
    private String bun;

    @Schema(description = "楼层id")
    private Long bfi;

    @Schema(description = "楼层名称")
    private String bfn;

    @Schema(description = "警告所属科室id")
    private Long sdi;

    @Schema(description = "科室id")
    private Long di;

    @Schema(description = "科室名称")
    private String dn;

    @Schema(description = "区域id")
    private Long ri;

    @Schema(description = "区域名称")
    private String rn;

    @Schema(description = "警告发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dt;

    @Schema(description = "排序时间（用户多次警报时置顶显示）前端可判断该时间是否发生变化来进行声音/闪耀提醒")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;

    @Schema(description = "处理人id")
    private Long uui;

    @Schema(description = "处理人姓名")
    private String uun;

    @Schema(description = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime udt;

    @Schema(description = "cctvUrl-可能多个逗号隔开")
    private String cctvUrl;

    public void setUa(SystemAlarmState state) {
        this.ua = state.getKey();
    }

}
