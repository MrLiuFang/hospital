package com.lion.person.entity.person.dto;

import com.lion.person.entity.enums.TransferState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午8:53
 */

@Data
@ApiModel
public class ReceivePatientDto {


    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    private Long patientId;

    @ApiModelProperty(value = "转移状态")
    @NotNull(message = "转移状态不能为空")
    private TransferState state = TransferState.FINISH;


}
