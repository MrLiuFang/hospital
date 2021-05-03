package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //位置(人员,设备......)
 * @Date 2021/5/1 上午11:24
 **/
@Data
@Document(value = "position")
public class Position implements Serializable {

    @Id
    private String _id;

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
