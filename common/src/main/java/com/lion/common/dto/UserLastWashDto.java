package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午4:41
 **/
@Data
public class UserLastWashDto implements Serializable {
    private static final long serialVersionUID = -90000050L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备
     */
    private Long monitorId;

    /**
     * 设备
     */
    private Long starId;

    /**
     * 最后洗手发生时间
     */
    private LocalDateTime dateTime;

    /**
     * 洗了多长时间（秒）
     */
    private Integer time;

    /**
     * 上次洗手
     */
    private UserLastWashDto previous;
}