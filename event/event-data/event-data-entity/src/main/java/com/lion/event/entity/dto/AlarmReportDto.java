package com.lion.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/23 上午10:14
 */
@Data
@ApiModel
public class AlarmReportDto {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "员工编号（30字符）")
    @Length(max = 30,message = "员工编号不能超过30字符")
    private String number;

    @ApiModelProperty(value = "汇报内容（100字符）")
    @Length(max = 100,message = "员工编号不能超过100字符")
    private String report;

}
