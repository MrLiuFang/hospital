package com.lion.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/27 下午4:33
 */
@Data
public class UpdatePositionLeaveTimeDto implements Serializable {

    private static final long serialVersionUID = 687982540812673444L;

    private Long pi;

    //上次时间
    private LocalDateTime pddt;
    //上次时间
    private LocalDateTime psdt;
    //本次时间
    private LocalDateTime cddt;
    //本次时间
    private LocalDateTime csdt;
}
