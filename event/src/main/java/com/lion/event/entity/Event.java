package com.lion.event.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 上午9:45
 **/
@Data
@Document
public class Event implements Serializable {

    @Id
    private String _id;

    //用户id
    private Long ui;

    //Star 的 MacID
    private String si;
    //Monitor 的 ID
    //可能為空
    private String mi;
    //Monitor 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer mb;

    //Tag 的 ID
    private String ti;

    // Tag 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer tb;

    //Tag 按下按鈕
    //Value=1-4
    private Integer bi;

    //溫度
    private BigDecimal t;

    //濕度
    private BigDecimal h;

    //警告
    private String w;

    //時間
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dt;
}
