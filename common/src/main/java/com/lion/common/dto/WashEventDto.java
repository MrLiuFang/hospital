package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/18 下午3:51
 **/
@Data
public class WashEventDto extends WashRecordDto implements Serializable {

    /**
     * 用于统计所有员工(仅限sql语法统计,无实际含义)
     */
    private Integer a = 0;

    /**
     * 洗手事件类型 (com.lion.common.enums.WashEventType)
     */
    private Integer wet;


    /**
     * 是否触发警告
     */
    private Boolean ia = false;

    /**
     * 触发警告原因(com.lion.manage.entity.enums.SystemAlarmType)
     */
    private Integer at;

    /**
     * 洗手时间(针对区域洗手规则-有记录为违规洗手,没有记录为错过洗手)定时洗手规则一律为错过洗手
     */
    private LocalDateTime wt = LocalDateTime.parse("9999-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    /**
     * 触发警告时间
     */
    private LocalDateTime adt = LocalDateTime.now();
}
