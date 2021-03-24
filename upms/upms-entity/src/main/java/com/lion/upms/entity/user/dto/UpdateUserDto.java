package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午8:54
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"username","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateUserDto extends AddUserDto {

}
