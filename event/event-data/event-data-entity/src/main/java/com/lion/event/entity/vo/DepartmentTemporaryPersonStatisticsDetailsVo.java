package com.lion.event.entity.vo;

import com.lion.person.entity.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午9:38
 */
@Data
@Schema
public class DepartmentTemporaryPersonStatisticsDetailsVo {
    @Schema(description = "流动人员总数")
    private Integer temporaryPersonCount = 0;

    @Schema(description = "正常流动人员总数")
    private Integer normalTemporaryPersonCount = 0;

    @Schema(description = "异常流动人员总数")
    private Integer abnormalTemporaryPersonCount= 0;

    @Schema(description = "部门信息")
    private List<TemporaryPersonDepartmentVo> temporaryPersonDepartmentVos;

    @Schema
    @Data
    public static class TemporaryPersonDepartmentVo {

        @Schema(description = "科室id")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;

        @Schema(description = "流动人员信息")
        private List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo> temporaryPersonVos;
    }

    @Schema
    @Data
    public static class TemporaryPersonVo{

        @Schema(description = "id")
        private Long id;

        @Schema(description = "头像（文件id）")
        private Long headPortrait;

        @Schema(description = "头像")
        private String headPortraitUrl;

        @Schema(description = "姓名")
        private String name;

        @Schema(description = "标签码")
        private String tagCode;

        @Schema(description = "标签电量")
        private Integer battery;

        @Schema(description = "是否异常")
        private com.lion.person.entity.enums.State deviceState = State.NORMAL;

    }
}
