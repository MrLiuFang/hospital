package com.lion.person.entity.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:27
 */
@Data
@ApiModel
public class AddTempLeaveDto {

    @ApiModelProperty(value = "患者id")
    @Size(min = 1,message = "请选择患者")
    private List<Long> patientIds;

    @ApiModelProperty(value = "员工编号")
    @NotNull(message = "请输入员工编号")
    private Integer number;

    @ApiModelProperty(value = "离开开始时间")
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @ApiModelProperty(value = "离开结束时间")
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ApiModelProperty(value = "离开原因")
    @NotBlank(message = "请填写离开原因")
    private String remarks;
}
