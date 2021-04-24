package com.lion.event.dto;

import com.lion.device.entity.device.Device;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午4:41
 **/
@Data
public class UserLastWashDto {


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备
     */
    Device monitor;

    /**
     * 设备
     */
    Device star;

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
