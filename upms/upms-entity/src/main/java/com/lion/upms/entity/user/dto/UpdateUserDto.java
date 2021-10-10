package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午8:54
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"deviceState","lastDataTime","username","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateUserDto extends AddUserDto {


}
