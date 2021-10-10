package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:57
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"deviceState","lastDataTime","id","username","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddUserDto extends User {

    @Schema(description = "是否创建账号")
    private Boolean isCreateAccount;

    @Schema(description = "所属科室ID")
    @NotNull(message = "{0000009}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @Schema(description = "负责科室ID")
    private List<Long> responsibleDepartmentIds;

    @Schema(description = "角色ID")
    private Long roleId;
}
