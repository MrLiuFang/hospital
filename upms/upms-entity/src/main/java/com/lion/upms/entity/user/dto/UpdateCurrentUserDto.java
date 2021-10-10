package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Column;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午9:32
 */
@Data
@Schema
public class UpdateCurrentUserDto{

    @Schema(description = "头像（文件id）")
    private Long headPortrait;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "新密码（md5密码)")
    private String newPassword;

    @Schema(description = "旧密码（md5密码)")
    private String oldPassword;
}
