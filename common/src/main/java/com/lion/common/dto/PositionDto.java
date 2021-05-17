package com.lion.common.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午4:20
 **/
@Data
public class PositionDto {

    @Id
    private String _id;

    //类型 (com.lion.event.entity.enums.Type)
    private Integer typ;

    /**
     * 员工/患者/流动人员id
     */
    private Long pi;

    /**
     * 设备/资产id
     */
    private Long adi;

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

    //设备产生時間
    private LocalDateTime ddt;

    //系统接受到时间
    private LocalDateTime sdt;
}
