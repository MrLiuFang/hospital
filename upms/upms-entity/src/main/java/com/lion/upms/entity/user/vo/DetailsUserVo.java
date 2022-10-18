package com.lion.upms.entity.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import org.springframework.util.StringUtils;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午3:04
 */
@Data
@Schema
public class DetailsUserVo extends User {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "所在科室名称")
    private String departmentName;

    @Schema(description = "所在科室id")
    private Long departmentId;

    @Schema(description = "头像url")
    private String headPortraitUrl;

    @Schema(description = "负责的科室")
    private List<ResponsibleDepartmentVo> responsibleDepartment;

    @Schema(description = "是否创建账号")
    private Boolean isCreateAccount;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private String alarmType;

    @Schema(description = "cctv-可能多个逗号隔开")
    private String cctv;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警报ID")
    private String alarmId;

    @Schema(description = "用户类型")
    private UserType userType;

    public Boolean getIsCreateAccount() {
        return (StringUtils.hasText(this.getUsername()) && StringUtils.hasText(this.getPassword()));
    }

    @Data
    @Schema
    public static class ResponsibleDepartmentVo{

        @Schema(description = "所负责的科室名称")
        private String departmentName;

        @Schema(description = "所负责的科室id")
        private Long departmentId;

        @Schema(description = "负责人")
        private List<ResponsibleUserVo> responsibleUser;

    }

    @Data
    @Schema
    public static class ResponsibleUserVo{
        @Schema(description = "负责人姓名")
        private String name;
        @Schema(description = "负责人头像")
        private String headPortraitUrl;

        @Schema(description = "负责人头像Id")
        private Long headPortrait;
    }

}

