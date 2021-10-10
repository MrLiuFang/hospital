package com.lion.upms.entity.role.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23上午9:59
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema
public class PageRoleVo extends Role {

    @Schema(description = "人数")
    private Integer userCount;
}
