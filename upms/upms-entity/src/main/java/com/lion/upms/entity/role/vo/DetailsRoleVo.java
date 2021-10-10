package com.lion.upms.entity.role.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.role.Role;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description: 角色详情
 * @date 2021/3/23上午10:28
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class DetailsRoleVo extends Role {

    @Schema(description = "用户")
    private List<DetailsRoleUserVo> users;
}


