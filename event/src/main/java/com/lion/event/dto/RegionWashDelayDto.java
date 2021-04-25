package com.lion.event.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午4:44
 **/
@Data
public class RegionWashDelayDto implements Serializable {
    private static final long serialVersionUID = -90000050L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 延迟推送时间
     */
    private LocalDateTime delayDateTime;

    /**
     * 区域id
     */
    private Long regionId;
}
