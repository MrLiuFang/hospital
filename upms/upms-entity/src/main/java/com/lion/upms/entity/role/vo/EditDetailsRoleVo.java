package com.lion.upms.entity.role.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.upms.entity.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description: 编辑角色基础信息获取详情
 * @date 2021/3/23上午11:21
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class EditDetailsRoleVo extends Role {
}
