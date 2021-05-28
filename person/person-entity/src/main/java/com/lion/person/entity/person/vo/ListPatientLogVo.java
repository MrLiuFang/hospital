package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.PatientLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午4:24
 */
@Data
@ApiModel
public class ListPatientLogVo extends PatientLog {

    @ApiModelProperty(value = "操作人姓名")
    private String userName;

    @ApiModelProperty(value = "操作人头像")
    private Long userHeadPortrait;

    @ApiModelProperty(value = "操作人头像")
    private String userHeadPortraitUrl;
}
