package com.lion.upms.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:57
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","username","password","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddUserDto extends User {

    @ApiModelProperty(value = "是否创建账号")
    private Boolean isCreateAccount;

    @ApiModelProperty(value = "所属科室ID")
    @NotNull(message = "所属科室不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long departmentId;

    @ApiModelProperty(value = "负责科室ID")
    private List<Long> responsibleDepartmentIds;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;
}
