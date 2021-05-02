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
@Document
public class Alarm implements Serializable {

    @Id
    private String _id;

    /**
     * 事件id
     */
    private String ei;

    //类型 (com.lion.event.entity.enums.Type)
    private Integer typ;

    /**
     * 员工/患者ID
     */
    private Long pi;

    /**
     * 设备ID
     */
    private Long dvi;

    /**
     * 警告id
     */
    private Long ai;

    /**
     * 警告名称
     */
    private Long an;

    /**
     * 发送告警时间
     */
    private LocalDateTime sdt;

}
