package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午4:44
 **/
@Data
public class RegionWashMonitorDelayDto implements Serializable {

    private static final long serialVersionUID = -6015687865551435784L;
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

    /**
     * 唯一事件标识
     */
    private String uuid;

    /**
     * 标签ID
     */
    private Long tagId;
}
