package com.lion.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/8 上午10:57
 */
@Data
@ApiModel
public class TempLeaveMonitorDto implements Serializable {

    private static final long serialVersionUID = 3832909425985165822L;
    private Long patientId;

    private LocalDateTime delayDateTime;
}
