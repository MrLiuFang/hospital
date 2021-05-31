package com.lion.person.entity.person.dto;

import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.TransferState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午8:53
 */

@Data
@ApiModel
public class ReceivePatientDto {


    @ApiModelProperty(value = "病床ID(该参数由接收转移患者后 修改新的床位带过来)")
    @NotNull(message = "病床ID不能为空")
    private Long newSickbedId;

    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者id不能为空")
    private Long patientId;

    @ApiModelProperty(value = "转移状态")
    @NotNull(message = "转移状态不能为空")
    private TransferState state = TransferState.FINISH;


}