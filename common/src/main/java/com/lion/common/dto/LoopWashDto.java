package com.lion.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/19 下午2:10
 **/
@Data
public class LoopWashDto {

    private Long userId;

    private LocalDateTime startWorkDateTime = LocalDateTime.now();

    private LocalDateTime endWorkDateTime;

    private LocalDateTime startWashDateTime;

    private LocalDateTime endWashDateTime;

    private LocalDateTime monitorDelayDateTime;

    private LocalDateTime alarmDateTime;

    private LocalDateTime deviceAlarmDateTime;

    private Integer count = 0;
}
