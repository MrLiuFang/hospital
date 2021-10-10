package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.event.entity.CurrentPosition;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.person.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午2:16
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class PatientDetailsVo extends Patient {

    @Schema(description = "最后为值（当前位置）")
    private CurrentRegionVo currentRegionVo;

    @Schema(description = "患者头像")
    private String headPortraitUrl;

    @Schema(description = "负责护士")
    private List<PatientDetailsVo.NurseVo> nurseVos;

    @Schema(description = "负责医生")
    private List<PatientDetailsVo.DoctorVo> doctorVos;

//    @Schema(description = "限制区域")
//    private List<RestrictedAreaVo> restrictedAreaVos;

    @Schema(description = "床位")
    private String bedCode;

    @Schema(description = "标签电量")
    private Integer battery;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private SystemAlarmType alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警告id")
    private String alarmId;

    @Schema(description = "汇报时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportDataTime;

    @Schema(description = "汇报内容")
    private String reportContent;

    @Schema(description = "汇报员工ID")
    private Long reportUserId;

    @Schema(description = "汇报员工姓名")
    private String reportUserName;

    @Schema(description = "汇报员工头像")
    private Long reportUserHeadPortrait;

    @Schema(description = "汇报员工头像url")
    private String reportUserHeadPortraitUrl;

    @Data
    @Schema
    public static class DoctorVo {
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
        @Schema(description = "负责护士姓名")
        private String nurseName;

        @Schema(description = "负责护士头像（文件id）")
        private Long nurseHeadPortrait;

        @Schema(description = "负责护士头像")
        private String nurseHeadPortraitUrl;
    }
}
