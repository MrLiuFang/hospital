package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.event.entity.SystemAlarm;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 上午9:08
 */
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class TemporaryPersonDetailsVo extends TemporaryPerson {

    @ApiModelProperty(value = "电量")
    private Integer battery;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private SystemAlarmType alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警告id")
    private String alarmId;

//    @ApiModelProperty(value = "限制区域")
//    private List<RestrictedAreaVo> restrictedAreaVos;
}
