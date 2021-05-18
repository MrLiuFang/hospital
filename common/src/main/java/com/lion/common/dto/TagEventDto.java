package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午8:31
 **/
@Data
public class TagEventDto extends TagRecordDto implements Serializable {

    //事件类型(com.lion.manage.entity.enums.SystemAlarmType)
    private Integer at;

    //最高温度
    private BigDecimal mxt;

    //最低温度
    private BigDecimal mit;

    //最高湿度
    private BigDecimal mxh;

    //最低湿度
    private BigDecimal mih;
}
