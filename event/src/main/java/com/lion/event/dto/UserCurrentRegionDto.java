package com.lion.event.dto;

import com.lion.manage.entity.region.Region;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午8:31
 **/
@Data
public class UserCurrentRegionDto {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前所在的区域
     */
    private Region region;

    /**
     * 第一次进入时间
     */
    private LocalDateTime firstEntryTime;
}
