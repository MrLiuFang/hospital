package com.lion.event.entity.vo;

import com.lion.aop.PageRequestInjection;
import com.lion.event.entity.Position;
import com.lion.event.entity.SystemAlarm;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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
    private UserCurrentRegionVo userCurrentRegionVo;

    @ApiModelProperty(value = "行动轨迹（当天（00:00:00 - 23:59:59））")
    private List<Position> positions;

    @ApiModelProperty(value = "警告信息（当天未处理的警告（00:00:00 - 23:59:59）")
    private List<SystemAlarm> systemAlarms;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

    @ApiModelProperty(value = "科室ID")
    private Long departmentId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

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
