package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class UpdateCurrentUserDto{

    @ApiModelProperty(value = "头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "密码（md5密码)")
    private String password;
}
