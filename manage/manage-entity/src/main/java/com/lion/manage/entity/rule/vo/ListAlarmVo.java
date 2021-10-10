package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.AlarmWay;
import com.lion.manage.entity.rule.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:45
 */
@Data
@Schema
public class ListAlarmVo extends Alarm {

    @Schema(description = "管理员")
    private List<DetailsAlarmVo.ManagerVo> managerVos;

    @Schema(description = "警报方式")
    private List<AlarmWay> ways;
}
