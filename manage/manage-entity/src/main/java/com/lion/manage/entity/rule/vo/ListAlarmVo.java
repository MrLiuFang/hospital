package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.AlarmWay;
import com.lion.manage.entity.rule.Alarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:45
 */
@Data
@ApiModel
public class ListAlarmVo extends Alarm {

    @ApiModelProperty(value = "管理员")
    private List<DetailsAlarmVo.ManagerVo> managerVos;

    @ApiModelProperty(value = "警报方式")
    private List<AlarmWay> ways;
}
