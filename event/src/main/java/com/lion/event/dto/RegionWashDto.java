package com.lion.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午7:04
 **/
@Data
public class RegionWashDto {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 区域id
     */
    private Long regionId;
}
