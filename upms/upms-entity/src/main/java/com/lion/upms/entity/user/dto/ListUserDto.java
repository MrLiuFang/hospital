package com.lion.upms.entity.user.dto;

import com.lion.core.persistence.Validator;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午8:49
 */
@Data
@ApiModel
public class ListUserDto {

    @ApiModelProperty(value = "所属科室ID")
    private Long departmentId;

    @ApiModelProperty(value = "员工类型(DOCTOR(0, \"医生\"), NURSE(1, \"护士\"))传字符和数字都可以")
    private UserType userType;

    @ApiModelProperty(value = "员工编号")
    private Integer number;

    @ApiModelProperty(value = "员工姓名")
    private String name;
}
