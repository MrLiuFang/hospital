package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 上午11:54
 */
@Data
@ApiModel
public class ListPatientTransferVo extends PatientTransfer {

    @ApiModelProperty(value = "患者信息")
    private PatientDetailsVo patientDetailsVo;

    @ApiModelProperty(value = "转移前科室名称")
    private String oldDepartmentName;

    @ApiModelProperty(value = "转移新科室名称")
    private String newDepartmentName;

    @ApiModelProperty(value = "转移前床位编码")
    private String oldSickbedCode;

    @ApiModelProperty(value = "转移新床位编码")
    private String newSickbedCode;

    @ApiModelProperty(value = "转移员工")
    private String ransferUserName;

    @ApiModelProperty(value = "转移员工头像")
    private Long ransferUserHeadPortrait;

    @ApiModelProperty(value = "转移员工头像")
    private String ransferUserHeadPortraitUrl;

    @ApiModelProperty(value = "接收员工")
    private String receiveUserName;

    @ApiModelProperty(value = "接收员工头像")
    private Long receiveUserHeadPortrait;

    @ApiModelProperty(value = "接收员工头像")
    private String receiveUserHeadPortraitUrl;
}
