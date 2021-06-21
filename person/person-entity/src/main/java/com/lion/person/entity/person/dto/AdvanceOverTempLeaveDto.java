package com.lion.person.entity.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.internal.dynalink.linker.LinkerServices;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午9:20
 */
@Data
@ApiModel
public class AdvanceOverTempLeaveDto {
    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    @Size(min = 1,message = "患者id不能为空")
    private List< Long> patientIds;
}
