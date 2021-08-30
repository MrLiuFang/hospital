package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
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

    @ApiModelProperty(value = "年龄")
    private int age;

    @ApiModelProperty(value = "病床编码")
    private String bedCode;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "转移新科室Id")
    private Long newDepartmentId;

    @ApiModelProperty(value = "转移新科室名称")
    private String newDepartmentName;

    @ApiModelProperty(value = "患者头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "负责护士")
    private List<NurseVo> nurseVos;

    @ApiModelProperty(value = "负责医生")
    private List<DoctorVo> doctorVos;

    @ApiModelProperty(value = "限制区域")
    private List<RestrictedAreaVo> restrictedAreaVoList;

    @ApiModelProperty(value = "临时离开权限")
    private TempLeaveVo tempLeaveVo;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private String alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警报ID")
    private String alarmId;

    @Data
    @ApiModel
    public static class DoctorVo {

        @ApiModelProperty(value = "负责医生id")
        private Long doctorId;

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

        @ApiModelProperty(value = "负责护士id")
        private Long nurseId;

        @ApiModelProperty(value = "负责护士姓名")
        private String nurseName;

        @ApiModelProperty(value = "负责护士头像（文件id）")
        private Long nurseHeadPortrait;

        @ApiModelProperty(value = "负责护士头像")
        private String nurseHeadPortraitUrl;
    }

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

    @Data
    @ApiModel
    public static class TempLeaveVo {

        @ApiModelProperty(value = "登记人id")
        private Long userId;

        @ApiModelProperty(value = "登记人姓名")
        private String userName;

        @ApiModelProperty(value = "登记人头像（文件id）")
        private Long headPortrait;

        @ApiModelProperty(value = "登记人头像（文件id）")
        private String headPortraitUrl;

        @ApiModelProperty(value = "离开开始时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDateTime;

        @ApiModelProperty(value = "离开结束时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDateTime;

    }
}
