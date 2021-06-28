package com.lion.common.dto;

import com.lion.common.enums.SystemAlarmState;
import com.lion.manage.entity.enums.SystemAlarmType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午8:15
 */
@Data
@ApiModel
public class SystemAlarmHandleDto implements Serializable {
    private static final long serialVersionUID = 7911333049643093624L;

    /**
     * 员工/患者/流动人员id
     */
    private Long peopleId;

    private Long regionId;

    private SystemAlarmState state;

}
