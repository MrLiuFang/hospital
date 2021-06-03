package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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
    private List<DepartmentPatientStatisticsDetailsVo.DepartmentVo> departmentVos;

    @ApiModel
    @Data
    public static class DepartmentVo{

        @ApiModelProperty(value = "科室id")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "患者信息")
        private List<DepartmentPatientStatisticsDetailsVo.PatientVo> patientVos;
    }

    @ApiModel
    @Data
    public static class PatientVo{

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
    }
}
