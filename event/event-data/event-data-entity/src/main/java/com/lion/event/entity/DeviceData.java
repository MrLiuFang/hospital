package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class DeviceData implements Serializable {

    private static final long serialVersionUID = -2540094505107531820L;
    @Id
    private String _id;

    @Schema(description = "tag id")
    private Long ti;

    @Schema(description = "tag code")
    private String tc;

    @Schema(description = "tag name")
    private String tn;

    @Schema(description = "monitor id")
    private Long mi;

    @Schema(description = "monitor code")
    private String mc;

    @Schema(description = "device id")
    private String di;

    @Schema(description = "device code")
    private String dc;

    @Schema(description = "monitor name")
    private String mn;

    @Schema(description = "star id")
    private Long si;

    @Schema(description = "star code")
    private String sc;

    @Schema(description = "star name")
    private String sn;

    @Schema(description = "事件")
    private String e;

    @Schema(description = "monitorRssi")
    private String mr;

    @Schema(description = "tagRssi")
    private String tr;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "设备产生時間")
    private LocalDateTime ddt;

    @Schema(description = "系统接受到时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
