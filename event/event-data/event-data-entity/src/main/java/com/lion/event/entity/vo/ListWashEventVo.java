package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.event.entity.WashEvent;
import com.lion.upms.entity.enums.Gender;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/6 上午10:03
 */
@Data
@ApiModel
public class ListWashEventVo {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "科室")
    private String departmentDame;

    @ApiModelProperty(value = "工号")
    private Integer number;

    @ApiModelProperty(value = "类型")
    private UserType userType;

    @ApiModelProperty(value = "性别")
    private Gender gender;

    @ApiModelProperty(value = "使用设备")
    private String deviceName;

    @ApiModelProperty(value = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useDateTime;

    @ApiModelProperty(value = "是否合规")
    private Boolean ia;
}
