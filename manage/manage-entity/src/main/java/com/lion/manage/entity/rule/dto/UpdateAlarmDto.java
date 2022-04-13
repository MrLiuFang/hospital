package com.lion.manage.entity.rule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.enums.AlarmWay;
import com.lion.manage.entity.rule.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:40
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateAlarmDto extends Alarm {

    @Schema(description = "警报方式(全量,先删后增)")
    private List<AlarmWay> ways;

    @Schema(description = "用户id(全量,先删后增)")
    private List<Long> userIds;
}
