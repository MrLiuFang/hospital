package com.lion.manage.entity.department.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:26
 */
@Data
@ApiModel
public class ResponsibleUserVo {

    @ApiModelProperty(value = "负责人id")
    private Long id;

    @ApiModelProperty(value = "负责人姓名")
    private String name;

    @ApiModelProperty(value = "负责人头像地址")
    private String headPortraitUrl;
}
