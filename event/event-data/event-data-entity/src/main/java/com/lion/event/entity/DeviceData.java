package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //事件原始数据
 * @Date 2021/4/23 上午9:45
 **/
@Data
@Document
public class DeviceData implements Serializable {

    @Id
    private String _id;

    //用户id(员工/患者)
    private Long pi;

    //类型 (com.lion.common.enums.Type)
    private Integer typ;

    //Star 的 code
    private String sc;

    //Star名称
    private String sn;

    //Star大类(com.lion.device.entity.enums.DeviceClassify)
    private Integer scl;

    //star类型(com.lion.device.entity.enums.DeviceType)
    private Integer st;

    //Monitor 的 code
    //可能為空
    private String mc;

    //Monitor名称
    //可能為空
    private String mn;

    //Monitor大类(com.lion.device.entity.enums.DeviceClassify)
    private Integer mcl;

    //Monitor类型(com.lion.device.entity.enums.DeviceType)
    private Integer mt;

    //Monitor 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer mb;

    //Tag 的 code
    private String tc;

    //Tag名称
    private String tn;

    //tag类型(com.lion.device.entity.enums.TagType)
    private Integer tt;

    //tag类型(com.lion.device.entity.enums.TagPurpose)
    private Integer tp;

    // Tag 的電量
    //0=正常
    //1=少於 90 天
    //2=少於 30 天
    private Integer tb;

    //Tag 按下按鈕
    //Value=1-4
    private Integer bi;

    //溫度
    private BigDecimal t;

    //濕度
    private BigDecimal h;

    //警告
    private String w;

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

    //设备产生時間
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ddt;

    //系统接受到时间
    private LocalDateTime sdt;
}
