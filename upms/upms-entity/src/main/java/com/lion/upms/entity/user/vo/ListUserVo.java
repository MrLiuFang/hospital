package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:08
 */
@Data
@ApiModel
public class ListUserVo extends User {

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "用户类型")
    private UserType userType;
}
