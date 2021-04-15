package com.lion.event.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/14上午9:45
 */
@Data
public class Event {

    private String starId;

    private String monitorId;

    private Integer monitorBattery;

    private String tagId;

    private Integer tagBattery;

    private Integer buttonId;

    private BigDecimal temperature;

    private BigDecimal humidity;

    private String warning;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}
