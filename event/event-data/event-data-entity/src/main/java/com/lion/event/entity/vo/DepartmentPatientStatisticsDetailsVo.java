package com.lion.event.entity.vo;

import com.lion.person.entity.enums.PatientState;
import com.lion.person.entity.enums.State;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 上午9:43
 */
@Data
@ApiModel
public class DepartmentPatientStatisticsDetailsVo {

    @ApiModelProperty(value = "患者总数")
    private Integer patientCount = 0;

    @ApiModelProperty(value = "正常患者总数")
    private Integer normalPatientCount = 0;

    @ApiModelProperty(value = "异常患者总数")
    private Integer abnormalPatientCount= 0;

    @ApiModelProperty(value = "部门信息")
    private List<PatientDepartmentVo> patientDepartmentVos;

    @ApiModel
    @Data
    public static class PatientDepartmentVo {

        @ApiModelProperty(value = "科室id")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "患者信息")
        private List<PatientVo> patientVos;
    }

    @ApiModel
    @Data
    public static class PatientVo{

        @ApiModelProperty(value = "患者id")
        private Long id;

        @ApiModelProperty(value = "头像（文件id）")
        private Long headPortrait;

        @ApiModelProperty(value = "头像")
        private String headPortraitUrl;

        @ApiModelProperty(value = "姓名")
        private String name;

        @ApiModelProperty(value = "标签码")
        private String tagCode;

        @ApiModelProperty(value = "床位")
        private String bedCode;

        @ApiModelProperty(value = "标签电量")
        private Integer battery;

        @ApiModelProperty(value = "是否异常")
        private com.lion.person.entity.enums.State deviceState = State.NORMAL;

        @ApiModelProperty(value = "病人状态")
        private PatientState patientState;
    }
}
