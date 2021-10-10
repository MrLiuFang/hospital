package com.lion.upms.entity.user.dto;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午9:29
 */
@Data
@Schema
public class ResetPasswordUserDto {

    @Schema(description = "用户ID")
    @NotNull(message = "{0000000}")
    private Long id;
}
