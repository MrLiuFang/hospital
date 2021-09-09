package com.lion.common.dto;

import com.lion.common.enums.SystemAlarmState;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.common.enums.Type;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午2:04
 **/
@Data
@ApiModel
public class SystemAlarmDto implements Serializable {

    private static final long serialVersionUID = -4723208189836453408L;
    /**
     * 唯一追踪标识 查入mongo后得到
     */
    private String id;

    /**
     * 警告类型（com.lion.common.enums.Type）
     */
    private Type type;


    /**
     * 员工/患者/流动人员id
     */
    private Long peopleId;

    /**
     * 资产id
     */
    private Long assetsId;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 温度
     */
    private BigDecimal temperature;

    /**
     * 温度
     */
    private BigDecimal humidity;

    /**
     * 警告类型
     */
    private SystemAlarmType systemAlarmType;

    /**
     * 警告发生时间
     */
    private LocalDateTime dateTime;

    /**
     * 警告延迟时间
     */
    private LocalDateTime delayDateTime;

    /**
     * 记录警告次数
     */
    private Integer count = 1;
}
