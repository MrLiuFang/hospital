package com.lion.upms.entity.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23上午9:44
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddRoleDto extends Role {

}
