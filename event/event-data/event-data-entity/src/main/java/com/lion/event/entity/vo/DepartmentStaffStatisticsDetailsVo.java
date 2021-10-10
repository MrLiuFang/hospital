package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.enums.State;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/21 下午8:46
 */
@Data
@Schema
public class DepartmentStaffStatisticsDetailsVo {


    @Schema(description = "员工总数")
    private Integer staffCount = 0;

    @Schema(description = "正常员工总数")
    private Integer normalStaffCount= 0;

    @Schema(description = "异常员工总数")
    private Integer abnormalStaffCount= 0;

    @Schema(description = "部门信息")
    private List<DepartmentStaffStatisticsDetailsVo.DepartmentVo> departmentVos;

    @Schema
    @Data
    public static class DepartmentVo{

        @Schema(description = "科室id")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;

        @Schema(description = "科室员工信息")
        private List<DepartmentStaffVo> departmentStaffVos;
    }

    @Schema
    @Data
    public static class DepartmentStaffVo implements Comparable<DepartmentStaffVo>{

        @Schema(description = "员工ID")
        private Long userId;

        @Schema(description = "员工姓名")
        private String userName;

        @Schema(description = "员工类型")
        private UserType userType;

        @Schema(description = "员工编号")
        private Integer number;

        @Schema(description = "头像（文件id）")
        private Long headPortrait;

        @Schema(description = "头像（url）")
        private String headPortraitUrl;

        @Schema(description = "最近的按钮事件")
        private String tagRuleEffect;

        @Schema(description = "按钮id（1，2，3，4-绿，红，黄，底部）")
        private Integer buttonId;

        @Schema(description = "最近的按钮事件时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime tagRuleEffectDateTime;

        @Schema(description = "标签电量")
        private Integer battery;

        @Schema(description = "标签编码")
        private String tagCode;

        @Schema(description = "是否异常")
        private State deviceState = State.NORMAL;

        @Schema(description = "是否在科室区域内")
        private Boolean isInRegion = true;

        @Override
        public int compareTo(DepartmentStaffVo o) {
            return Objects.equals(isInRegion,true)?1:-1;
        }
    }
}
