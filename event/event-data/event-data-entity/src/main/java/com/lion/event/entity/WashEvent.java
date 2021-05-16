package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午2:18
 **/
@Data
@Document(value = "wash_event")
public class WashEvent extends WashRecord {

    @Id
    private String _id;

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
     * 警告是否已知熟(已处理)ia=false时该值无实际意义
     */
    private Boolean iua = false;

    /**
     * 系统时间(触发时间)
     */
    private LocalDateTime sdt;

    /**
     * 触发警告原因(com.lion.common.enums.WashEventAlarmType)
     */
    private Integer at;

    /**
     * 洗手时间(针对区域洗手规则-有记录为违规洗手,没有记录为错过洗手)定时洗手规则一律为错过洗手
     */
    private LocalDateTime wt = LocalDateTime.parse("9999-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

}
