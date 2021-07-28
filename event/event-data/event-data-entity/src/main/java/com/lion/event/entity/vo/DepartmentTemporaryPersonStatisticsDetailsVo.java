package com.lion.event.entity.vo;

import com.lion.person.entity.enums.State;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午9:38
 */
@Data
@ApiModel
public class DepartmentTemporaryPersonStatisticsDetailsVo {
    @ApiModelProperty(value = "流动人员总数")
    private Integer temporaryPersonCount = 0;

    @ApiModelProperty(value = "正常流动人员总数")
    private Integer normalTemporaryPersonCount = 0;

    @ApiModelProperty(value = "异常流动人员总数")
    private Integer abnormalTemporaryPersonCount= 0;

    @ApiModelProperty(value = "部门信息")
    private List<TemporaryPersonDepartmentVo> temporaryPersonDepartmentVos;

    @ApiModel
    @Data
    public static class TemporaryPersonDepartmentVo {

        @ApiModelProperty(value = "科室id")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "流动人员信息")
        private List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo> temporaryPersonVos;
    }

    @ApiModel
    @Data
    public static class TemporaryPersonVo{

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "头像（文件id）")
        private Long headPortrait;

        @ApiModelProperty(value = "头像")
        private String headPortraitUrl;

        @ApiModelProperty(value = "姓名")
        private String name;

        @ApiModelProperty(value = "标签码")
        private String tagCode;

        @ApiModelProperty(value = "标签电量")
        private Integer battery;

        @ApiModelProperty(value = "是否异常")
        private com.lion.person.entity.enums.State deviceState = State.NORMAL;

    }
}
