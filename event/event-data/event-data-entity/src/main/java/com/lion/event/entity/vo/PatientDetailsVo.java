package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.event.entity.CurrentPosition;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/3 下午2:16
 */
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = { "updateDateTime", "createUserId", "updateUserId"}
)
public class PatientDetailsVo extends Patient {

    @ApiModelProperty(value = "最后为值（当前位置）")
    private CurrentRegionVo currentRegionVo;

    @ApiModelProperty(value = "患者头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "负责护士")
    private List<PatientDetailsVo.NurseVo> nurseVos;

    @ApiModelProperty(value = "负责医生")
    private List<PatientDetailsVo.DoctorVo> doctorVos;

    @ApiModelProperty(value = "限制区域")
    private List<RestrictedAreaVo> restrictedAreaVos;

    @ApiModelProperty(value = "床位")
    private String bedCode;

    @ApiModelProperty(value = "标签电量")
    private Integer battery;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private SystemAlarmType alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警告id")
    private String alarmId;

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
