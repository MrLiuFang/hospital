package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.enums.Hygiene;
import com.lion.common.enums.Type;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/14上午9:45
 */
@Data
public class DeviceDataDto implements Serializable {

    private static final long serialVersionUID = 4692994096293827415L;
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

    private String monitorRssi;

    private String tagRssi;

    //時間
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    //标签类型
    private Type tagType;

    //是否洗手设备
    private Hygiene hygiene;

    //系统接受到时间
    private LocalDateTime systemDateTime = LocalDateTime.now();

}
