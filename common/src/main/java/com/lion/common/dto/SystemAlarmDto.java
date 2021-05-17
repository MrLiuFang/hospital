package com.lion.common.dto;

import com.lion.common.enums.SystemAlarmType;
import com.lion.common.enums.Type;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午2:04
 **/
@Data
@ApiModel
public class SystemAlarmDto implements Serializable {

    /**
     * 唯一追踪标识
     */
    private String uuid;

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
