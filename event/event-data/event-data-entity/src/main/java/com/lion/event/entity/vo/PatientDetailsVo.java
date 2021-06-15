package com.lion.event.entity.vo;

import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午2:16
 */
@Data
@ApiModel
public class PatientDetailsVo extends Patient {

    @ApiModelProperty(value = "最后为值（当前位置）")
    private CurrentRegionVo currentRegionVo;

    @ApiModelProperty(value = "患者头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "负责护士")
    private List<PatientDetailsVo.NurseVo> nurseVos;

    @ApiModelProperty(value = "负责医生")
    private List<PatientDetailsVo.DoctorVo> doctorVos;

    @ApiModelProperty(value = "床位")
    private String bedCode;

    @ApiModelProperty(value = "标签电量")
    private Integer battery;


    @Data
    @ApiModel
    public static class DoctorVo {
        @ApiModelProperty(value = "负责医生姓名")
        private String doctorName;

        @ApiModelProperty(value = "负责医生头像（文件id）")
        private Long doctorHeadPortrait;

        @ApiModelProperty(value = "负责医生头像")
        private String doctorHeadPortraitUrl;
    }

    @Data
    @ApiModel
    public static class NurseVo {
        @ApiModelProperty(value = "负责护士姓名")
        private String nurseName;

        @ApiModelProperty(value = "负责护士头像（文件id）")
        private Long nurseHeadPortrait;

        @ApiModelProperty(value = "负责护士头像")
        private String nurseHeadPortraitUrl;
    }
}
