package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.TagRuleEffect;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/21 下午8:46
 */
@Data
@ApiModel
public class DepartmentStaffStatisticsDetails {

    @ApiModelProperty(value = "科室id")
    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "员工总数")
    private Integer staffCount;

    @ApiModelProperty(value = "正常员工总数")
    private Integer normalStaffCount;

    @ApiModelProperty(value = "异常员工总数")
    private Integer abnormalStaffCount;

    @ApiModelProperty(value = "员工信息")
    private List<DepartmentStaff> departmentStaffs;

    @ApiModel
    @Data
    public static class DepartmentStaff{

        @ApiModelProperty(value = "员工ID")
        private Long userId;

        @ApiModelProperty("员工姓名")
        private String userName;

        @ApiModelProperty("员工类型")
        private UserType type;

        @ApiModelProperty("员工编号")
        private Integer number;

        @ApiModelProperty(value = "头像（文件id）")
        private Long headPortrait;

        @ApiModelProperty(value = "头像（url）")
        private String headPortraitUrl;

        @ApiModelProperty(value = "最近的按钮事件")
        private TagRuleEffect tagRuleEffect;

        @ApiModelProperty(value = "最近的按钮事件时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime tagRuleEffectDateTime;

        @ApiModelProperty(value = "标签电量")
        private Integer battery;
    }
}
