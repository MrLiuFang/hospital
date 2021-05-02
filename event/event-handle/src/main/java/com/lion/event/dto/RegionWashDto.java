package com.lion.event.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午7:04
 **/
@Data
public class RegionWashDto implements Serializable {
    private static final long serialVersionUID = -90000050L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 区域id
     */
    private Long regionId;
}
