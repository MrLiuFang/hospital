package com.lion.event.entity.vo;

import com.lion.person.entity.enums.PatientState;
import com.lion.person.entity.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class DepartmentPatientStatisticsDetailsVo {

    @Schema(description = "患者总数")
    private Integer patientCount = 0;

    @Schema(description = "正常患者总数")
    private Integer normalPatientCount = 0;

    @Schema(description = "异常患者总数")
    private Integer abnormalPatientCount= 0;

    @Schema(description = "部门信息")
    private List<PatientDepartmentVo> patientDepartmentVos;

    @Schema
    @Data
    public static class PatientDepartmentVo {

        @Schema(description = "科室id")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;

        @Schema(description = "患者信息")
        private List<PatientVo> patientVos;
    }

    @Schema
    @Data
    public static class PatientVo{

        @Schema(description = "患者id")
        private Long id;

        @Schema(description = "头像（文件id）")
        private Long headPortrait;

        @Schema(description = "头像")
        private String headPortraitUrl;

        @Schema(description = "姓名")
        private String name;

        @Schema(description = "标签码")
        private String tagCode;

        @Schema(description = "床位")
        private String bedCode;

        @Schema(description = "标签电量")
        private Integer battery;

        @Schema(description = "是否异常")
        private com.lion.person.entity.enums.State deviceState = State.NORMAL;

//        @Schema(description = "病人状态")
//        private PatientState patientState;
    }
}
