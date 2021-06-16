package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.print.DocFlavor;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午3:04
 */
@Data
@ApiModel
public class DetailsUserVo extends User {

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "所在科室名称")
    private String departmentName;

    @ApiModelProperty(value = "所在科室id")
    private Long departmentId;

    @ApiModelProperty(value = "头像url")
    private String headPortraitUrl;

    @ApiModelProperty(value = "负责的科室")
    private List<ResponsibleDepartmentVo> responsibleDepartment;

    @ApiModelProperty(value = "是否创建账号")
    private Boolean isCreateAccount;

    public Boolean getIsCreateAccount() {
        return (StringUtils.hasText(this.getUsername()) && StringUtils.hasText(this.getPassword()));
    }

    @Data
    @ApiModel
    public static class ResponsibleDepartmentVo{

        @ApiModelProperty("所负责的科室名称")
        private String departmentName;

        @ApiModelProperty("所负责的科室id")
        private Long departmentId;

        @ApiModelProperty(value = "负责人")
        private List<ResponsibleUserVo> responsibleUser;

    }

    @Data
    @ApiModel
    public static class ResponsibleUserVo{
        @ApiModelProperty(value = "负责人姓名")
        private String name;
        @ApiModelProperty(value = "负责人头像")
        private String headPortraitUrl;
    }

}

