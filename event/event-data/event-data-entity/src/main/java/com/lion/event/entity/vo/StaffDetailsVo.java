package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.event.entity.Position;
import com.lion.event.entity.SystemAlarm;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.upms.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午4:38
 */
@Data
@Schema
public class StaffDetailsVo extends User {

    @Schema(description = "最后为值（当前位置）")
    private CurrentRegionVo currentRegionVo;

    @Schema(description = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

    @Schema(description = "科室ID")
    private Long departmentId;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private SystemAlarmType alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警告id")
    private String alarmId;

    @Schema(description = "负责的科室")
    private List<DepartmentResponsibleVo> departmentResponsibleVos;

    @Data
    @Schema
    public static class DepartmentResponsibleVo {

        @Schema(description = "科室ID")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;
    }
}
