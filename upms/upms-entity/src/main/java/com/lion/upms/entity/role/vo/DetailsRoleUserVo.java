package com.lion.upms.entity.role.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23上午10:30
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"username","id","email","headPortrait","gender","birthday","phoneNumber","address","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsRoleUserVo extends User {

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "头像地址")
    private String headPortraitUrl;
}
