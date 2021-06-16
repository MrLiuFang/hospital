package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/19 下午8:27
 */
@Data
public class LoopWashDeviceAlarmDto implements Serializable {

    private static final long serialVersionUID = 6542097293690040154L;
    private Long userId;

    private String uuid;

    private LocalDateTime startAlarmDateTime;

    private LocalDateTime endAlarmDateTime;

    private LocalDateTime deviceDelayAlarmDateTime;

    private Integer count = 0;
}
