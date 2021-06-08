package com.lion.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/8 上午10:57
 */
@Data
@ApiModel
public class TempLeaveMonitorDto {

    private Long patientId;

    private LocalDateTime delayDateTime;
}
