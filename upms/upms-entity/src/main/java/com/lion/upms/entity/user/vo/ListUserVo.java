package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:08
 */
@Data
@Schema
public class ListUserVo extends User {

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "用户类型")
    private UserType userType;

    @Schema(description = "是否创建账号")
    private Boolean isCreateAccount;
}
