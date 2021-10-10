package com.lion.upms.entity.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.Collection;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午8:49
 */
@Data
@Schema
public class ListUserDto {

    @Schema(description = "所属科室ID")
    private Long departmentId;

    @Schema(description = "员工类型Id")
    private Collection<Long> userTypeIds;

    @Schema(description = "员工编号")
    private Integer number;

    @Schema(description = "员工姓名")
    private String name;
}
