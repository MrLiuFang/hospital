package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/5下午3:09
 */
@Data
@ApiModel
public class CurrentUserDetailsVo extends User {

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "所在科室名称")
    private String departmentName;

    @ApiModelProperty(value = "所在科室id")
    private Long departmentId;

    @ApiModelProperty(value = "头像url")
    private String headPortraitUrl;

    @ApiModelProperty(value = "权限")
    private String resources;
}
