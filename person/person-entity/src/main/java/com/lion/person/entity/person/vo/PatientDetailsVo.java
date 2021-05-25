package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午2:24
 */
@Data
@ApiModel
public class PatientDetailsVo extends Patient {

    @ApiModelProperty(value = "绑定患者")
    private PatientDetailsVo bindPatient;

    @ApiModelProperty(value = "病床编码")
    private String bedCode;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "患者头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "负责护士姓名")
    private String nurseName;

    @ApiModelProperty(value = "负责医生姓名")
    private String doctorName;

    @ApiModelProperty(value = "负责护士头像（文件id）")
    private Long nurseHeadPortrait;

    @ApiModelProperty(value = "负责医生头像（文件id）")
    private Long doctorHeadPortrait;

    @ApiModelProperty(value = "负责护士头像")
    private String nurseHeadPortraitUrl;

    @ApiModelProperty(value = "负责医生头像")
    private String doctorHeadPortraitUrl;

    @ApiModelProperty(value = "限制区域")
    private List<RestrictedAreaVo> restrictedAreaVoList;

    @Data
    @ApiModel
    public static class RestrictedAreaVo{

        @ApiModelProperty(value = "区域id")
        private Long regionId;

        @ApiModelProperty(value = "区域名称")
        private String regionName;

        @ApiModelProperty(value = "建筑名称")
        private String buildName;

        @ApiModelProperty(value = "建筑楼成名称")
        private String buildFloorName;

        @ApiModelProperty(value = "备注")
        private String remark;
    }
}
