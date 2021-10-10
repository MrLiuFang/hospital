package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.event.entity.SystemAlarm;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 上午9:08
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class TemporaryPersonDetailsVo extends TemporaryPerson {

    @Schema(description = "电量")
    private Integer battery;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private SystemAlarmType alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警告id")
    private String alarmId;

//    @Schema(description = "限制区域")
//    private List<RestrictedAreaVo> restrictedAreaVos;
}
