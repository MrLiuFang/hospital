package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 上午11:54
 */
@Data
@Schema
public class ListPatientTransferVo extends PatientTransfer {



    @Schema(description = "患者信息")
    private PatientDetailsVo patientDetailsVo;

    @Schema(description = "转移前科室名称")
    private String oldDepartmentName;

    @Schema(description = "转移新科室名称")
    private String newDepartmentName;

    @Schema(description = "转移前床位编码")
    private String oldSickbedCode;

    @Schema(description = "转移新床位编码")
    private String newSickbedCode;

    @Schema(description = "转移员工")
    private String ransferUserName;

    @Schema(description = "转移员工头像")
    private Long ransferUserHeadPortrait;

    @Schema(description = "转移员工头像")
    private String ransferUserHeadPortraitUrl;

    @Schema(description = "接收员工")
    private String receiveUserName;

    @Schema(description = "接收员工头像")
    private Long receiveUserHeadPortrait;

    @Schema(description = "接收员工头像")
    private String receiveUserHeadPortraitUrl;
}
