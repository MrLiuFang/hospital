package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:42
 **/
@Data
@Document(value = "wash")
public class Wash implements Serializable {
    @Id
    private String _id;

    /**
     * 员工/患者id
     */
    private Long pi;

    /**
     * 洗手的设备id
     */
    private Long dvi;

    /**
     * 洗手的设备名称
     */
    private String dvn;

    /**
     * 洗手的设备编码
     */
    private String dvc;

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
    private LocalDateTime ddt;

    //系统接受到时间
    private LocalDateTime sdt;
}
