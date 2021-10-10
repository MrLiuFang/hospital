package com.lion.device.entity.fault.vo;

import com.lion.device.entity.fault.Fault;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午9:57
 */
@Data
@Schema
public class FaultDetailsVo extends Fault {

    @Schema(description = "所属区域名称")
    @Column(name = "region_name")
    private String regionName;

    @Schema(description = "所属建筑名称")
    @Column(name = "build_name")
    private String buildName;

    @Schema(description = "所属建筑楼层名称")
    @Column(name = "build_floor_name")
    private String buildFloorName;

    @Schema(description = "所属科室名称")
    @Column(name = "department_name")
    private String departmentName;
}
