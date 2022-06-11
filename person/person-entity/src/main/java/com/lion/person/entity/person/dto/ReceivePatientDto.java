package com.lion.person.entity.person.dto;

import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午8:53
 */

@Data
@Schema
public class ReceivePatientDto extends Patient {


    @Schema(description = "病床ID(该参数由接收转移患者后 修改新的床位带过来)")
    private Long newSickbedId;

    @Schema(description = "患者id")
    @NotNull(message = "患者id不能为空",groups = {Validator.OtherOne.class})
    private Long patientId;

    @Schema(description = "转移状态")
    @NotNull(message = "{1000028}",groups = {Validator.OtherOne.class})
    private TransferState state = TransferState.FINISH;


}
