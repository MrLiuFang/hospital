package com.lion.upms.entity.role.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23上午10:30
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"username","id","email","headPortrait","gender","birthday","phoneNumber","address","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsRoleUserVo extends User {

    @ApiModelProperty("科室名称")
    private String departmentName;

    @ApiModelProperty("头像地址")
    private String headPortraitUrl;
}
