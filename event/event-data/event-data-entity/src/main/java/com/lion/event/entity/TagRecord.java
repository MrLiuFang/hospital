package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 下午5:36
 **/
@Data
@Document(value = "tag_record")
public class TagRecord implements Serializable {
    @Id
    private String _id;

    //类型 (com.lion.event.entity.enums.Type)
    private Integer typ;

    /**
     * tagid
     */
    private Long ti;

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

    //温度
    private BigDecimal t;

    //湿度
    private BigDecimal h;

    //设备产生時間
    private LocalDateTime ddt;

    //系统接受到时间
    private LocalDateTime sdt;


}
