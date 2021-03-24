package com.lion.upms.entity.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午9:29
 */
@Data
@ApiModel
public class ResetPasswordUserDto {

    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long id;
}
