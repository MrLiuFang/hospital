package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private Long dvi;

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
     * 警告发生时间
     */
    private LocalDateTime dt;

    /**
     * 排序时间（用户多次警报时置顶显示）
     */
    private LocalDateTime sdt;

    public void setSdt(LocalDateTime sdt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.sdt = LocalDateTime.parse(dtf.format(sdt),dtf);
    }
}
