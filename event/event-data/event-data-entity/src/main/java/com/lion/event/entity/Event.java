package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
     * 原始数据id(触发事件id)
     */
    private Long ddi;

    //类型 (com.lion.event.entity.enums.Type)
    private Integer typ;

    /**
     * 员工/患者id
     */
    private Long pi;

    /**
     * 设备id
     */
    private Long dvi;

    /**
     * 科室id
     */
    private Long di;

    /**
     * 科室名称
     */
    private Long dn;

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
     * 触发警告时间
     */
    private LocalDateTime adt;

    /**
     * 触发警告原因
     */
    private Integer at;

    /**
     * 解除警告时间
     */
    private LocalDateTime radt;

    /**
     * 解除警告原始数据ID
     */
    private String reoi;


}
