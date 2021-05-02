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
@Document
public class Event {

    @Id
    private String _id;

    /**
     * 事件唯一标识
     */
    private String ui;


    //类型 (com.lion.common.enums.Type)
    private Integer typ;

    /**
     * 员工/患者id
     */
    private Long pi;

    /**
     * 设备id
     */
    private Long dvi;

    //建筑id
    private Long bui;

    //建筑名称
    private String bun;

    //楼层id
    private Long bfi;

    //楼层名称
    private String bfn;

    /**
     * 科室id
     */
    private Long di;

    /**
     * 科室名称
     */
    private String dn;

    //区域id
    private Long ri;

    //区域名称
    private String rn;

    /**
     * 是否触发警告
     */
    private Boolean ia;

    /**
     * 触发警告的规则id
     */
    private Long rui;

    /**
     * 触发警告的规则名称
     */
    private Long run;

    /**
     * 系统时间
     */
    private LocalDateTime sdt;

    /**
     * 触发警告原因(com.lion.common.enums.EventType)
     */
    private Integer at;

    /**
     * 解除警告时间
     */
    private LocalDateTime uadt = LocalDateTime.parse("9999-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    //解除警告原因(com.lion.common.enums.RemoveAlarmType)
    private Integer uat = 99;

}
