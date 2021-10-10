package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/5下午3:09
 */
@Data
@Schema
public class CurrentUserDetailsVo extends User {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "所在科室名称")
    private String departmentName;

    @Schema(description = "所在科室id")
    private Long departmentId;

    @Schema(description = "头像url")
    private String headPortraitUrl;

    @Schema(description = "权限")
    private String resources;
}
