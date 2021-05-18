package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //警告事件
 * @Date 2021/5/1 上午11:24
 **/
@Data
@Document(value = "system_alarm")
public class SystemAlarm implements Serializable {

    @Id
    private String _id;

    /**
     * 唯一追踪标识
     */
    private String ui;

    /**
     * 警告类型(com.lion.common.enums.Type）)
     */
    private Integer ty;

    /**
     * 员工/患者/流动人员id
     */
    private Long pi;

    /**
     * 资产id
     */
    private Long ai;

    /**
     * 设备id
     */
    private Long di;

    /**
     * 标签id
     */
    private Long ti;

    /**
     * 警告id
     */
    private Long ali;

    /**
     * 警告类型(com.lion.manage.entity.enums.SystemAlarmType)
     */
    private Integer sat;

    /**
     * 是否已知熟（处理）
     */
    private Boolean ua = false;

    /**
     * 警告发生时间
     */
    private LocalDateTime dt;

    /**
     * 排序时间（用户多次警报时置顶显示）
     */
    private LocalDateTime sdt;

}
