package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.event.entity.Position;
import com.lion.event.entity.SystemAlarm;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午4:38
 */
@Data
@ApiModel
public class StaffDetailsVo extends User {

    @ApiModelProperty(value = "最后为值（当前位置）")
    private CurrentRegionVo currentRegionVo;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

    @ApiModelProperty(value = "科室ID")
    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private SystemAlarmType alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警告id")
    private String alarmId;

    @ApiModelProperty(value = "负责的科室")
    private List<DepartmentResponsibleVo> departmentResponsibleVos;

    @Data
    @ApiModel
    public static class DepartmentResponsibleVo {

        @ApiModelProperty(value = "科室ID")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;
    }
}
