package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:29
 */
@Data
@Schema
public class ListWardVo extends Ward {

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "房间数量")
    private Integer roomQuantity = 0;

    @Schema(description = "病床数量")
    private Integer sickbedQuantity = 0;
}
