package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/19 下午2:10
 **/
@Data
public class LoopWashDto implements Serializable {

    private static final long serialVersionUID = -1898767569626930575L;
    private Long userId;

    private String uuid;

    private LocalDateTime startWorkDateTime = LocalDateTime.now();

    private LocalDateTime endWorkDateTime;

    private LocalDateTime startWashDateTime;

    private LocalDateTime endWashDateTime;

    private LocalDateTime monitorDelayDateTime;

    private Integer count = 0;
}
