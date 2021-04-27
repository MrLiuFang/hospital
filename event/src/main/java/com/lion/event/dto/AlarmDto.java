package com.lion.event.dto;

import com.lion.event.entity.enums.AlarmType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午5:28
 **/
@Data
public class AlarmDto implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 区域Id 发生警告的区域
     */
    private Long regionId;

    /**
     * 警告类型
     */
    private AlarmType alarmType;

    /**
     * 告警产生时间（用于解除警告用，如果发生警告的时候检测用户最后一次洗手时间大于该时间就可以解除警告）
     */
    private LocalDateTime alarmDateTime;

    /**
     * 延迟推送时间
     */
    private LocalDateTime delayDateTime;

}
