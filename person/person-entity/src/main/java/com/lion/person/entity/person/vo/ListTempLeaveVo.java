package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.TempLeave;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午9:47
 */
@Data
@ApiModel
public class ListTempLeaveVo extends TempLeave {

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "患者名字")
    private String patientName;

    @ApiModelProperty(value = "头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "登记人姓名")
    private String userName;

    @ApiModelProperty(value = "登记人头像（文件id）")
    private Long userHeadPortrait;

    @ApiModelProperty(value = "登记人头像")
    private String userHeadPortraitUrl;
}
