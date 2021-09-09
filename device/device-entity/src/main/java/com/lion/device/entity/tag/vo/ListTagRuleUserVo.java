package com.lion.device.entity.tag.vo;

import com.lion.core.persistence.Validator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:47
 **/
@Data
@ApiModel
public class ListTagRuleUserVo {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "用户职位")
    private String position;

    @ApiModelProperty(value = "员工编号")
    private Integer number;

    @ApiModelProperty(value = "头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;
}
