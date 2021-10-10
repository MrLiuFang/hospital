package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.person.entity.person.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class PatientDetailsVo extends Patient {

    @Schema(description = "绑定患者")
    private PatientDetailsVo bindPatient;

    @Schema(description = "年龄")
    private int age;

    @Schema(description = "病床编码")
    private String bedCode;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "转移新科室Id")
    private Long newDepartmentId;

    @Schema(description = "转移新科室名称")
    private String newDepartmentName;

    @Schema(description = "患者头像")
    private String headPortraitUrl;

    @Schema(description = "负责护士")
    private List<NurseVo> nurseVos;

    @Schema(description = "负责医生")
    private List<DoctorVo> doctorVos;

//    @Schema(description = "限制区域")
//    private List<RestrictedAreaVo> restrictedAreaVoList;

    @Schema(description = "临时离开权限")
    private TempLeaveVo tempLeaveVo;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private String alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警报ID")
    private String alarmId;

    @Data
    @Schema
    public static class DoctorVo {

        @Schema(description = "负责医生id")
        private Long doctorId;

        @Schema(description = "负责医生姓名")
        private String doctorName;

        @Schema(description = "负责医生头像（文件id）")
        private Long doctorHeadPortrait;

        @Schema(description = "负责医生头像")
        private String doctorHeadPortraitUrl;
    }

    @Data
    @Schema
    public static class NurseVo {

        @Schema(description = "负责护士id")
        private Long nurseId;

        @Schema(description = "负责护士姓名")
        private String nurseName;

        @Schema(description = "负责护士头像（文件id）")
        private Long nurseHeadPortrait;

        @Schema(description = "负责护士头像")
        private String nurseHeadPortraitUrl;
    }

//    @Data
//    @Schema
//    public static class RestrictedAreaVo{
//
//        @Schema(description = "区域id")
//        private Long regionId;
//
//        @Schema(description = "区域名称")
//        private String regionName;
//
//        @Schema(description = "建筑名称")
//        private String buildName;
//
//        @Schema(description = "建筑楼成名称")
//        private String buildFloorName;
//
//        @Schema(description = "备注")
//        private String remark;
//    }

    @Data
    @Schema
    public static class TempLeaveVo {

        @Schema(description = "登记人id")
        private Long userId;

        @Schema(description = "登记人姓名")
        private String userName;

        @Schema(description = "登记人头像（文件id）")
        private Long headPortrait;

        @Schema(description = "登记人头像（文件id）")
        private String headPortraitUrl;

        @Schema(description = "离开开始时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDateTime;

        @Schema(description = "离开结束时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDateTime;

    }
}
