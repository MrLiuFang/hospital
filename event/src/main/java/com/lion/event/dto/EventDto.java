package com.lion.event.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/14上午9:45
 */
@Data
public class EventDto {

    //Star 的 MacID
    private String starId;
    //Monitor 的 ID
    //可能為空
    private String monitorId;
    //Monitor 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer monitorBattery;

    //Tag 的 ID
    private String tagId;

    // Tag 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer tagBattery;

    //Tag 按下按鈕
    //Value=1-4
    private Integer buttonId;

    //溫度
    private BigDecimal temperature;

    //濕度
    private BigDecimal humidity;

    //警告
    private String warning;

    //時間
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}
